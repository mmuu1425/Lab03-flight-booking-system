package com.example.gatewayservice.dto;

import java.util.UUID;

public class TicketResponse {
    private UUID ticketUid;
    private String flightNumber;
    private String fromAirport;  // 新增字段
    private String toAirport;    // 新增字段
    private String date;         // 新增字段
    private Integer price;
    private String status;

    public TicketResponse() {}

    public TicketResponse(UUID ticketUid, String flightNumber, String fromAirport,
                          String toAirport, String date, Integer price, String status) {
        this.ticketUid = ticketUid;
        this.flightNumber = flightNumber;
        this.fromAirport = fromAirport;
        this.toAirport = toAirport;
        this.date = date;
        this.price = price;
        this.status = status;
    }

    // Getters and Setters
    public UUID getTicketUid() { return ticketUid; }
    public void setTicketUid(UUID ticketUid) { this.ticketUid = ticketUid; }

    public String getFlightNumber() { return flightNumber; }
    public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }

    public String getFromAirport() { return fromAirport; }  // 新增getter
    public void setFromAirport(String fromAirport) { this.fromAirport = fromAirport; }

    public String getToAirport() { return toAirport; }      // 新增getter
    public void setToAirport(String toAirport) { this.toAirport = toAirport; }

    public String getDate() { return date; }                // 新增getter
    public void setDate(String date) { this.date = date; }

    public Integer getPrice() { return price; }
    public void setPrice(Integer price) { this.price = price; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}