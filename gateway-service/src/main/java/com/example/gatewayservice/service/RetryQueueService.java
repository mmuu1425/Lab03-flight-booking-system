// RetryQueueService.java - 简化可编译版本
package com.example.gatewayservice.service;

import com.example.gatewayservice.dto.RetryTask;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.*;
import java.util.*;

@Service
public class RetryQueueService {

    private final BlockingQueue<RetryTask> queue = new LinkedBlockingQueue<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final RestTemplate restTemplate;

    @Value("${service.bonus.url}")
    private String bonusServiceUrl;

    public RetryQueueService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostConstruct
    public void init() {
        // 每30秒重试一次
        scheduler.scheduleAtFixedRate(() -> {
            try {
                processRetryQueue();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 30, 30, TimeUnit.SECONDS);
    }

    public void addTask(RetryTask task) {
        queue.offer(task);
        System.out.println("Added retry task: " + task.getTaskType() + " for ticket: " + task.getTicketUid());
    }
    private void processRetryQueue() {
        System.out.println("Processing retry queue, size: " + queue.size());
        System.out.println("当前时间: " + new java.util.Date());

        RetryTask task = queue.poll();
        if (task != null) {
            System.out.println("处理任务: " + task.getTicketUid() +
                    ", 尝试次数: " + task.getRetryCount());

            try {
                executeRetryTask(task);
                System.out.println("✓ 重试成功");

            } catch (Exception e) {
                task.incrementRetryCount();

                if (task.getRetryCount() < 5) {  // 增加到5次
                    // 计算延迟时间（指数退避）
                    long delaySeconds = calculateDelay(task.getRetryCount());
                    System.out.println("✗ 重试失败，等待 " + delaySeconds + " 秒");

                    // 使用scheduler延迟执行
                    scheduler.schedule(() -> {
                        System.out.println("延迟后重新加入: " + task.getTicketUid());
                        queue.offer(task);
                    }, delaySeconds, TimeUnit.SECONDS);

                } else {
                    System.err.println("✗ 任务失败超过5次: " + task.getTicketUid());
                }
            }
        }
    }

    private long calculateDelay(int retryCount) {
        // 指数退避：30, 60, 120, 240, 480秒
        return 30L * (long) Math.pow(2, retryCount - 1);
    }
    @SuppressWarnings("unchecked")
    private void executeRetryTask(RetryTask task) throws Exception {
        String taskType = task.getTaskType();

        if ("UPDATE_BONUS_PURCHASE".equals(taskType)) {
            Map<String, Object> purchaseData = (Map<String, Object>) task.getRequestData();
            callBonusServiceForPurchase(task.getUsername(), task.getTicketUid(), purchaseData);
        } else if ("UPDATE_BONUS_REFUND".equals(taskType)) {
            callBonusServiceForRefund(task.getUsername(), task.getTicketUid());
        } else {
            throw new IllegalArgumentException("Unknown task type: " + taskType);
        }
    }

    private void callBonusServiceForPurchase(String username, UUID ticketUid, Map<String, Object> requestData) {
        try {
            String url = bonusServiceUrl + "/api/v1/privilege/process-purchase";

            Map<String, Object> requestBody = Map.of(
                    "username", username,
                    "ticketUid", ticketUid.toString(),
                    "price", requestData.get("price"),
                    "paidFromBalance", requestData.getOrDefault("paidFromBalance", false)
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            restTemplate.exchange(url, HttpMethod.POST, entity, Object.class);

        } catch (Exception e) {
            throw new RuntimeException("Bonus service call failed: " + e.getMessage());
        }
    }

    private void callBonusServiceForRefund(String username, UUID ticketUid) {
        try {
            String url = bonusServiceUrl + "/api/v1/privilege/process-refund";

            Map<String, Object> requestBody = Map.of(
                    "username", username,
                    "ticketUid", ticketUid.toString()
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            restTemplate.exchange(url, HttpMethod.POST, entity, Void.class);

        } catch (Exception e) {
            throw new RuntimeException("Bonus service call failed: " + e.getMessage());
        }
    }
}