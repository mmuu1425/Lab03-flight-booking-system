package com.example.gatewayservice.service;

import com.example.gatewayservice.dto.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FallbackService {

    // 航班列表的fallback响应
    public Object getFallbackFlights() {
        Map<String, Object> fallbackResponse = new HashMap<>();
        fallbackResponse.put("items", Collections.emptyList());
        fallbackResponse.put("page", 1);
        fallbackResponse.put("pageSize", 10);
        fallbackResponse.put("totalElements", 0);
        return fallbackResponse;
    }

    // 用户信息的fallback响应
    public UserInfoResponse getFallbackUserInfo(String username) {
        List<TicketResponse> emptyTickets = new ArrayList<>();
        // 根据测试要求，Bonus服务不可用时返回空的privilege
        return new UserInfoResponse(emptyTickets, new PrivilegeShortInfo());
    }

    // 票务列表的fallback响应
    public List<TicketResponse> getFallbackTickets() {
        return new ArrayList<>();
    }

    // 单个票务的fallback响应
    public TicketResponse getFallbackTicket(String ticketUid) {
        TicketResponse fallback = new TicketResponse();
        fallback.setTicketUid(java.util.UUID.fromString(ticketUid));
        fallback.setFlightNumber("N/A");
        fallback.setFromAirport("Unknown");
        fallback.setToAirport("Unknown");
        fallback.setDate("N/A");
        fallback.setPrice(0);
        fallback.setStatus("UNKNOWN");
        return fallback;
    }

    // FallbackService.java - 修改 privilege 相关方法
    public PrivilegeResponse getFallbackPrivilege(String username) {
        // 直接抛出异常，控制器会转换为503响应
        throw new ServiceUnavailableException("Bonus Service unavailable");
    }

    public Object getFallbackPurchaseTicket(String username, Object request) {
        // 直接抛出异常
        throw new ServiceUnavailableException("Bonus Service unavailable");
    }
}