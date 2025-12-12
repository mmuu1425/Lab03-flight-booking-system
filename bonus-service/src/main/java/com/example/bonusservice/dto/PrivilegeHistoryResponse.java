package com.example.bonusservice.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PrivilegeHistoryResponse {
    private String date;
    private String ticketUid;
    private Integer balanceDiff;
    private String operationType;

    public PrivilegeHistoryResponse() {}

    public PrivilegeHistoryResponse(String date, String ticketUid, Integer balanceDiff, String operationType) {
        this.date = date;
        this.ticketUid = ticketUid;
        this.balanceDiff = balanceDiff;
        this.operationType = operationType;
    }

    // Getters and Setters
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    // 符合Postman格式：yyyy-MM-ddTHH:mm:ssZ
    public void setDate(LocalDateTime dateTime) {
        if (dateTime != null) {
            // 格式化为：2021-10-08T19:59:19Z
            this.date = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
        }
    }

    public String getTicketUid() { return ticketUid; }
    public void setTicketUid(String ticketUid) { this.ticketUid = ticketUid; }

    public Integer getBalanceDiff() { return balanceDiff; }
    public void setBalanceDiff(Integer balanceDiff) { this.balanceDiff = balanceDiff; }

    public String getOperationType() { return operationType; }
    public void setOperationType(String operationType) { this.operationType = operationType; }
}