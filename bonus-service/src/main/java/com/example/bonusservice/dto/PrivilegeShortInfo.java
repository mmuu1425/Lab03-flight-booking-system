package com.example.bonusservice.dto;

public class PrivilegeShortInfo {
    private Integer balance;
    private String status;

    public PrivilegeShortInfo() {}

    public PrivilegeShortInfo(Integer balance, String status) {
        this.balance = balance;
        this.status = status;
    }

    // Getters and Setters
    public Integer getBalance() { return balance; }
    public void setBalance(Integer balance) { this.balance = balance; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}