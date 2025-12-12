package com.example.flightservice.dto;

import java.util.List;

public class FlightPaginationResponse {
    private int page;
    private int pageSize;
    private int totalElements;
    private List<FlightItemResponse> items;

    // 构造函数
    public FlightPaginationResponse() {}

    public FlightPaginationResponse(int page, int pageSize, int totalElements, List<FlightItemResponse> items) {
        this.page = page;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.items = items;
    }

    // Getters and Setters
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }

    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }

    public int getTotalElements() { return totalElements; }
    public void setTotalElements(int totalElements) { this.totalElements = totalElements; }

    public List<FlightItemResponse> getItems() { return items; }
    public void setItems(List<FlightItemResponse> items) { this.items = items; }
}