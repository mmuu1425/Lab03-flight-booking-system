package com.example.gatewayservice.dto;

public class PrivilegeHistoryResponse {
    private String date;
    private String ticketUid;
    private Integer balanceDiff;
    private String operationType;

    public PrivilegeHistoryResponse() {}

    // Getters and Setters
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTicketUid() { return ticketUid; }
    public void setTicketUid(String ticketUid) { this.ticketUid = ticketUid; }

    public Integer getBalanceDiff() { return balanceDiff; }
    public void setBalanceDiff(Integer balanceDiff) { this.balanceDiff = balanceDiff; }

    public String getOperationType() { return operationType; }
    public void setOperationType(String operationType) { this.operationType = operationType; }
}