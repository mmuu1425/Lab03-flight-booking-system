package com.example.bonusservice.dto;

import java.util.UUID;

public class RefundRequest {
    private String username;
    private UUID ticketUid;

    public RefundRequest() {}

    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public UUID getTicketUid() { return ticketUid; }
    public void setTicketUid(UUID ticketUid) { this.ticketUid = ticketUid; }
}