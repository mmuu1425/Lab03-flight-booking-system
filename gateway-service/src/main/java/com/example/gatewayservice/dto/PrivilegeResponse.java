package com.example.gatewayservice.dto;

import java.util.List;

public class PrivilegeResponse {
    private Integer balance;
    private String status;
    private List<PrivilegeHistoryResponse> history;

    public PrivilegeResponse() {}

    // Getters and Setters
    public Integer getBalance() { return balance; }
    public void setBalance(Integer balance) { this.balance = balance; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<PrivilegeHistoryResponse> getHistory() { return history; }
    public void setHistory(List<PrivilegeHistoryResponse> history) { this.history = history; }
}