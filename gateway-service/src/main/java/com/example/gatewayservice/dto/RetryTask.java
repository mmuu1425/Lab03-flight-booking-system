// RetryTask.java - 完整可编译版本
package com.example.gatewayservice.dto;

import java.util.UUID;

public class RetryTask {
    private String taskType; // "UPDATE_BONUS_PURCHASE", "UPDATE_BONUS_REFUND"
    private String username;
    private UUID ticketUid;
    private Object requestData;
    private int retryCount = 0;

    // 必须的构造函数
    public RetryTask() {}

    public RetryTask(String taskType, String username, UUID ticketUid, Object requestData, int retryCount) {
        this.taskType = taskType;
        this.username = username;
        this.ticketUid = ticketUid;
        this.requestData = requestData;
        this.retryCount = retryCount;
    }

    // 必须的getter/setter方法
    public String getTaskType() { return taskType; }
    public void setTaskType(String taskType) { this.taskType = taskType; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public UUID getTicketUid() { return ticketUid; }
    public void setTicketUid(UUID ticketUid) { this.ticketUid = ticketUid; }

    public Object getRequestData() { return requestData; }
    public void setRequestData(Object requestData) { this.requestData = requestData; }

    public int getRetryCount() { return retryCount; }
    public void setRetryCount(int retryCount) { this.retryCount = retryCount; }

    public void incrementRetryCount() { retryCount++; }
}