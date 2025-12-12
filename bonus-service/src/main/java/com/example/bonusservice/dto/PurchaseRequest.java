package com.example.bonusservice.dto;

import java.util.UUID;

public class PurchaseRequest {
    private String username;
    private UUID ticketUid;
    private Integer price;
    private boolean paidFromBalance;

    public PurchaseRequest() {}

    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public UUID getTicketUid() { return ticketUid; }
    public void setTicketUid(UUID ticketUid) { this.ticketUid = ticketUid; }

    public Integer getPrice() { return price; }
    public void setPrice(Integer price) { this.price = price; }

    public boolean isPaidFromBalance() { return paidFromBalance; }
    public void setPaidFromBalance(boolean paidFromBalance) { this.paidFromBalance = paidFromBalance; }
}