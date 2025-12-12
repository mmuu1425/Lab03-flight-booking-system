package com.example.gatewayservice.service;

import com.example.gatewayservice.dto.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class GatewayService {

    private final RestTemplate restTemplate;

    @Value("${service.flight.url}")
    private String serviceFlightUrl;

    @Value("${service.ticket.url}")
    private String serviceTicketUrl;

    @Value("${service.bonus.url}")
    private String serviceBonusUrl;

    public GatewayService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // 获取用户完整信息
    public UserInfoResponse getUserInfo(String username) {
        try {
            // 调用Ticket Service获取票务列表
            String ticketServiceUrl = serviceTicketUrl + "/api/v1/tickets";
            ResponseEntity<TicketResponse[]> ticketResponse = restTemplate.exchange(
                    ticketServiceUrl,
                    HttpMethod.GET,
                    new HttpEntity<>(createHeadersWithUsername(username)),
                    TicketResponse[].class
            );

            // 调用Bonus Service获取特权信息
            String bonusServiceUrl = serviceBonusUrl + "/api/v1/privilege";
            ResponseEntity<PrivilegeResponse> privilegeResponse = restTemplate.exchange(
                    bonusServiceUrl,
                    HttpMethod.GET,
                    new HttpEntity<>(createHeadersWithUsername(username)),
                    PrivilegeResponse.class
            );

            // 构建响应
            List<TicketResponse> tickets = Arrays.asList(ticketResponse.getBody());
            PrivilegeShortInfo privilege = new PrivilegeShortInfo(
                    privilegeResponse.getBody().getBalance(),
                    privilegeResponse.getBody().getStatus()
            );

            return new UserInfoResponse(tickets, privilege);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get user info", e);
        }
    }

    // 获取特权信息
    public PrivilegeResponse getPrivilegeInfo(String username) {
        try {
            String bonusServiceUrl = serviceBonusUrl + "/api/v1/privilege";
            ResponseEntity<PrivilegeResponse> response = restTemplate.exchange(
                    bonusServiceUrl,
                    HttpMethod.GET,
                    new HttpEntity<>(createHeadersWithUsername(username)),
                    PrivilegeResponse.class
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get privilege info", e);
        }
    }

    // 获取用户票务列表
    public List<TicketResponse> getUserTickets(String username) {
        try {
            String ticketServiceUrl = serviceTicketUrl + "/api/v1/tickets";
            ResponseEntity<TicketResponse[]> response = restTemplate.exchange(
                    ticketServiceUrl,
                    HttpMethod.GET,
                    new HttpEntity<>(createHeadersWithUsername(username)),
                    TicketResponse[].class
            );
            return Arrays.asList(response.getBody());
        } catch (Exception e) {
            throw new RuntimeException("Failed to get user tickets", e);
        }
    }

    // 获取特定票务
    public TicketResponse getUserTicket(String username, String ticketUid) {
        try {
            String ticketServiceUrl = serviceTicketUrl + "/api/v1/tickets/" + ticketUid;
            ResponseEntity<TicketResponse> response = restTemplate.exchange(
                    ticketServiceUrl,
                    HttpMethod.GET,
                    new HttpEntity<>(createHeadersWithUsername(username)),
                    TicketResponse.class
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get user ticket", e);
        }
    }

    // 购买票务
    public Object purchaseTicket(String username, Object purchaseRequest) {
        try {
            String ticketServiceUrl = serviceTicketUrl + "/api/v1/tickets";
            HttpHeaders headers = createHeadersWithUsername(username);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Object> requestEntity = new HttpEntity<>(purchaseRequest, headers);
            ResponseEntity<Object> response = restTemplate.exchange(
                    ticketServiceUrl,
                    HttpMethod.POST,
                    requestEntity,
                    Object.class
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to purchase ticket", e);
        }
    }

    // 退票
    public boolean returnTicket(String username, String ticketUid) {
        try {
            String ticketServiceUrl = serviceTicketUrl + "/api/v1/tickets/" + ticketUid;
            ResponseEntity<Void> response = restTemplate.exchange(
                    ticketServiceUrl,
                    HttpMethod.DELETE,
                    new HttpEntity<>(createHeadersWithUsername(username)),
                    Void.class
            );
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            throw new RuntimeException("Failed to return ticket", e);
        }
    }
    // 获取航班列表
    public Object getFlights(int page, int size) {
        try {
            String flightServiceUrl = serviceFlightUrl + "/api/v1/flights?page=" + page + "&size=" + size;
            ResponseEntity<Object> response = restTemplate.getForEntity(flightServiceUrl, Object.class);
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get flights", e);
        }
    }
    // 创建包含用户名的请求头
    private HttpHeaders createHeadersWithUsername(String username) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Name", username);
        return headers;
    }
}