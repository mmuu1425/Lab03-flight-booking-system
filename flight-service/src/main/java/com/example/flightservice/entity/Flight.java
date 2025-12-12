package com.example.flightservice.entity;

import jakarta.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "flight")
public class Flight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "flight_number", nullable = false)
    private String flightNumber;

    @Column(name = "datetime", nullable = false)
    private ZonedDateTime datetime;

    @ManyToOne
    @JoinColumn(name = "from_airport_id", referencedColumnName = "id")
    private Airport fromAirport;

    @ManyToOne
    @JoinColumn(name = "to_airport_id", referencedColumnName = "id")
    private Airport toAirport;

    @Column(name = "price", nullable = false)
    private Integer price;

    // Constructors
    public Flight() {}

    public Flight(String flightNumber, ZonedDateTime datetime, Airport fromAirport, Airport toAirport, Integer price) {
        this.flightNumber = flightNumber;
        this.datetime = datetime;
        this.fromAirport = fromAirport;
        this.toAirport = toAirport;
        this.price = price;
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getFlightNumber() { return flightNumber; }
    public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }

    public ZonedDateTime getDatetime() { return datetime; }
    public void setDatetime(ZonedDateTime datetime) { this.datetime = datetime; }

    public Airport getFromAirport() { return fromAirport; }
    public void setFromAirport(Airport fromAirport) { this.fromAirport = fromAirport; }

    public Airport getToAirport() { return toAirport; }
    public void setToAirport(Airport toAirport) { this.toAirport = toAirport; }

    public Integer getPrice() { return price; }
    public void setPrice(Integer price) { this.price = price; }
}