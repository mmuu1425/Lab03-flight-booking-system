package com.example.flightservice.dto;

public class FlightItemResponse {
    private String flightNumber;
    private String fromAirport;
    private String toAirport;
    private String date;
    private Integer price;

    // 构造函数
    public FlightItemResponse() {}

    public FlightItemResponse(String flightNumber, String fromAirport, String toAirport, String date, Integer price) {
        this.flightNumber = flightNumber;
        this.fromAirport = fromAirport;
        this.toAirport = toAirport;
        this.date = date;
        this.price = price;
    }

    // Getters and Setters
    public String getFlightNumber() { return flightNumber; }
    public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }

    public String getFromAirport() { return fromAirport; }
    public void setFromAirport(String fromAirport) { this.fromAirport = fromAirport; }

    public String getToAirport() { return toAirport; }
    public void setToAirport(String toAirport) { this.toAirport = toAirport; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public Integer getPrice() { return price; }
    public void setPrice(Integer price) { this.price = price; }
}