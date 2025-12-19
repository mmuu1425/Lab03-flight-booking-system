package com.example.gatewayservice.service;

import com.example.gatewayservice.dto.*;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CircuitBreakerGatewayService {

    private final RestTemplate restTemplate;
    private final FallbackService fallbackService;
    private final RetryQueueService retryQueueService;

    @Value("${service.flight.url}")
    private String serviceFlightUrl;

    @Value("${service.ticket.url}")
    private String serviceTicketUrl;

    @Value("${service.bonus.url}")
    private String serviceBonusUrl;

    public CircuitBreakerGatewayService(RestTemplate restTemplate,
                                        FallbackService fallbackService,
                                        RetryQueueService retryQueueService) {
        this.restTemplate = restTemplate;
        this.fallbackService = fallbackService;
        this.retryQueueService = retryQueueService;
    }

    // ============ 查询操作（带Circuit Breaker） ============

    @CircuitBreaker(name = "flightService", fallbackMethod = "getFlightsFallback")
    public Object getFlights(int page, int size) {
        String url = serviceFlightUrl + "/api/v1/flights?page=" + page + "&size=" + size;
        ResponseEntity<Object> response = restTemplate.getForEntity(url, Object.class);
        return response.getBody();
    }

    public Object getFlightsFallback(int page, int size, Exception e) {
        return fallbackService.getFallbackFlights();
    }

    @CircuitBreaker(name = "ticketService", fallbackMethod = "getUserInfoFallback")
    public UserInfoResponse getUserInfo(String username) {
        // 1. 获取tickets（必须成功）
        List<TicketResponse> tickets = getUserTickets(username);

        // 2. 尝试获取privilege（可能失败）
        PrivilegeShortInfo privilege = null;
        try {
            PrivilegeResponse privilegeResponse = getPrivilegeInfo(username);
            privilege = new PrivilegeShortInfo(
                    privilegeResponse.getBalance(),
                    privilegeResponse.getStatus()
            );
        } catch (Exception e) {
            throw new ServiceUnavailableException("Bonus Service unavailable");  // 正确！
        }

        return new UserInfoResponse(tickets, privilege);
    }

    public UserInfoResponse getUserInfoFallback(String username, Exception e) {
        return fallbackService.getFallbackUserInfo(username);
    }

    @CircuitBreaker(name = "bonusService", fallbackMethod = "getPrivilegeInfoFallback")
    public PrivilegeResponse getPrivilegeInfo(String username) {
        String url = serviceBonusUrl + "/api/v1/privilege";
        HttpHeaders headers = createHeadersWithUsername(username);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<PrivilegeResponse> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, PrivilegeResponse.class
        );
        return response.getBody();
    }

    public PrivilegeResponse getPrivilegeInfoFallback(String username, Exception e) {
        // 根据测试，返回503错误
        throw new ServiceUnavailableException("Bonus Service unavailable");
    }

    @CircuitBreaker(name = "ticketService", fallbackMethod = "getUserTicketsFallback")
    public List<TicketResponse> getUserTickets(String username) {
        String url = serviceTicketUrl + "/api/v1/tickets";
        HttpHeaders headers = createHeadersWithUsername(username);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<TicketResponse[]> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, TicketResponse[].class
        );
        return Arrays.asList(response.getBody());
    }

    public List<TicketResponse> getUserTicketsFallback(String username, Exception e) {
        return fallbackService.getFallbackTickets();
    }

    @CircuitBreaker(name = "ticketService", fallbackMethod = "getUserTicketFallback")
    public TicketResponse getUserTicket(String username, String ticketUid) {
        String url = serviceTicketUrl + "/api/v1/tickets/" + ticketUid;
        HttpHeaders headers = createHeadersWithUsername(username);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<TicketResponse> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, TicketResponse.class
        );
        return response.getBody();
    }

    public TicketResponse getUserTicketFallback(String username, String ticketUid, Exception e) {
        return fallbackService.getFallbackTicket(ticketUid);
    }

    // ============ 购买票务操作（关键修改） ============

    @CircuitBreaker(name = "purchaseService", fallbackMethod = "purchaseTicketFallback")
    @SuppressWarnings("unchecked")
    public Object purchaseTicket(String username, Object purchaseRequest) {
        System.out.println("=== [Gateway] 开始购买机票 ===");

        try {
            // 1. 创建机票（主操作）
            String ticketUrl = serviceTicketUrl + "/api/v1/tickets";
            HttpHeaders headers = createHeadersWithUsername(username);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Object> entity = new HttpEntity<>(purchaseRequest, headers);

            ResponseEntity<Map> ticketResponse = restTemplate.exchange(
                    ticketUrl, HttpMethod.POST, entity, Map.class
            );

            Map<String, Object> ticketData = ticketResponse.getBody();
            String ticketUidStr = (String) ticketData.get("ticketUid");
            UUID ticketUid = UUID.fromString(ticketUidStr);
            Integer price = (Integer) ticketData.get("price");

            System.out.println("✓ 机票创建成功: " + ticketUidStr);

            // 2. 检查Bonus Service是否可用
            boolean bonusAvailable = true;
            try {
                String healthUrl = serviceBonusUrl + "/manage/health";
                ResponseEntity<String> healthResponse = restTemplate.getForEntity(healthUrl, String.class);
                bonusAvailable = healthResponse.getStatusCode() == HttpStatus.OK;
            } catch (Exception e) {
                bonusAvailable = false;
            }

            if (!bonusAvailable) {
                System.out.println("✗ Bonus Service不可用，将加入重试队列");

                // 将Bonus更新任务加入重试队列
                Map<String, Object> requestMap = (Map<String, Object>) purchaseRequest;
                Boolean paidFromBalance = (Boolean) requestMap.getOrDefault("paidFromBalance", false);

                Map<String, Object> retryData = new HashMap<>();
                retryData.put("username", username);
                retryData.put("ticketUid", ticketUidStr);
                retryData.put("price", price);
                retryData.put("paidFromBalance", paidFromBalance);

                RetryTask retryTask = new RetryTask(
                        "UPDATE_BONUS_PURCHASE",
                        username,
                        ticketUid,
                        retryData,
                        0
                );
                retryQueueService.addTask(retryTask);
                System.out.println("✓ 已加入重试队列");

                // 抛异常返回503（但机票已创建，队列已有任务）
                throw new ServiceUnavailableException("Bonus Service unavailable");
            }

            // 3. Bonus Service可用，正常调用
            System.out.println("✓ Bonus Service可用，正常调用");

            Map<String, Object> requestMap = (Map<String, Object>) purchaseRequest;
            Boolean paidFromBalance = (Boolean) requestMap.getOrDefault("paidFromBalance", false);

            String bonusUrl = serviceBonusUrl + "/api/v1/privilege/process-purchase";
            Map<String, Object> bonusRequest = Map.of(
                    "username", username,
                    "ticketUid", ticketUidStr,
                    "price", price,
                    "paidFromBalance", paidFromBalance
            );

            HttpEntity<Map<String, Object>> bonusEntity = new HttpEntity<>(bonusRequest, headers);

            try {
                ResponseEntity<Map> bonusResponse = restTemplate.exchange(
                        bonusUrl, HttpMethod.POST, bonusEntity, Map.class
                );

                // 合并响应
                if (bonusResponse.getStatusCode().is2xxSuccessful()) {
                    Map<String, Object> bonusData = bonusResponse.getBody();
                    if (bonusData != null) {
                        if (bonusData.containsKey("paidByMoney")) {
                            ticketData.put("paidByMoney", bonusData.get("paidByMoney"));
                        }
                        if (bonusData.containsKey("paidByBonuses")) {
                            ticketData.put("paidByBonuses", bonusData.get("paidByBonuses"));
                        }
                        if (bonusData.containsKey("privilege")) {
                            ticketData.put("privilege", bonusData.get("privilege"));
                        }
                    }
                }

                System.out.println("✓ Bonus Service调用成功");

            } catch (Exception bonusError) {
                System.out.println("✗ Bonus Service调用失败，加入重试队列");

                // 将Bonus更新任务加入重试队列
                Map<String, Object> retryData = new HashMap<>();
                retryData.put("username", username);
                retryData.put("ticketUid", ticketUidStr);
                retryData.put("price", price);
                retryData.put("paidFromBalance", paidFromBalance);

                RetryTask retryTask = new RetryTask(
                        "UPDATE_BONUS_PURCHASE",
                        username,
                        ticketUid,
                        retryData,
                        0
                );
                retryQueueService.addTask(retryTask);
                System.out.println("✓ 已加入重试队列");

                // 抛异常返回503（但机票已创建，队列已有任务）
                throw new ServiceUnavailableException("Bonus Service unavailable");
            }

            System.out.println("=== [Gateway] 购买流程完全成功 ===");
            return ticketData;

        } catch (Exception e) {
            System.out.println("=== [Gateway] 购买失败: " + e.getMessage() + " ===");

            // 如果是ServiceUnavailableException，直接抛出
            if (e instanceof ServiceUnavailableException) {
                throw e;
            }

            // 其他异常（如Ticket Service失败）
            throw new ServiceUnavailableException("Service unavailable");
        }
    }

    public Object purchaseTicketFallback(String username, Object purchaseRequest, Exception e) {
        System.out.println("=== [Gateway] purchaseTicket fallback触发 ===");
        // 直接抛异常，返回503
        throw new ServiceUnavailableException("Bonus Service unavailable");
    }

    // ============ 退票操作（关键修改） ============

    public boolean returnTicket(String username, String ticketUid) {
        // 1. 调用Ticket Service退票
        String ticketUrl = serviceTicketUrl + "/api/v1/tickets/" + ticketUid;
        HttpHeaders headers = createHeadersWithUsername(username);
        HttpEntity<Void> ticketEntity = new HttpEntity<>(headers);

        ResponseEntity<Void> ticketResponse = restTemplate.exchange(
                ticketUrl, HttpMethod.DELETE, ticketEntity, Void.class
        );

        if (!ticketResponse.getStatusCode().is2xxSuccessful()) {
            return false;
        }

        // 2. 尝试调用Bonus Service
        try {
            String bonusUrl = serviceBonusUrl + "/api/v1/privilege/process-refund";
            Map<String, Object> bonusRequest = Map.of(
                    "username", username,
                    "ticketUid", ticketUid
            );

            HttpEntity<Map<String, Object>> bonusEntity = new HttpEntity<>(bonusRequest, headers);
            restTemplate.exchange(bonusUrl, HttpMethod.POST, bonusEntity, Void.class);

            return true;

        } catch (Exception e) {
            // 3. Bonus Service失败：加入重试队列
            System.out.println("Bonus Service unavailable for refund, adding to retry queue: " + ticketUid);

            RetryTask retryTask = new RetryTask(
                    "UPDATE_BONUS_REFUND",
                    username,
                    UUID.fromString(ticketUid),
                    null, // 退款不需要额外数据
                    0
            );
            retryQueueService.addTask(retryTask);

            // 4. 返回成功（因为机票已退，只是bonus没更新）
            return true;
        }
    }

    // ============ 辅助方法 ============

    private HttpHeaders createHeadersWithUsername(String username) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Name", username);
        return headers;
    }
}