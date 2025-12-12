package com.example.ticketservice.service;

import com.example.ticketservice.dto.TicketPurchaseResponse;
import com.example.ticketservice.dto.TicketResponse;
import com.example.ticketservice.entity.Ticket;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TicketService {

    // 获取用户所有票
    List<TicketResponse> getUserTickets(String username);

    // 获取用户特定票
    Optional<TicketResponse> getUserTicket(String username, UUID ticketUid);

    // 购买票
    TicketPurchaseResponse purchaseTicket(String username, String flightNumber, Integer price, Boolean paidFromBalance);

    // 退票
    boolean cancelTicket(String username, UUID ticketUid);

    // 检查票是否存在
    boolean ticketExists(UUID ticketUid);
}