package com.example.ticketservice.service;

import com.example.ticketservice.dto.TicketPurchaseResponse;
import com.example.ticketservice.dto.TicketResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TicketService {
    List<TicketResponse> getUserTickets(String username);
    Optional<TicketResponse> getUserTicket(String username, UUID ticketUid);
    TicketPurchaseResponse purchaseTicket(String username, String flightNumber,
                                          Integer price, Boolean paidFromBalance);
    boolean cancelTicket(String username, UUID ticketUid);
    boolean ticketExists(UUID ticketUid);
}