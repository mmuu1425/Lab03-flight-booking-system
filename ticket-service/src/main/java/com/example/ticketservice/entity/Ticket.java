package com.example.ticketservice.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "ticket")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ticket_uid", unique = true, nullable = false)
    private UUID ticketUid;

    @Column(name = "username", nullable = false, length = 80)
    private String username;

    @Column(name = "flight_number", nullable = false, length = 20)
    private String flightNumber;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    // Constructors
    public Ticket() {
        this.ticketUid = UUID.randomUUID();
    }

    public Ticket(String username, String flightNumber, Integer price, String status) {
        this();
        this.username = username;
        this.flightNumber = flightNumber;
        this.price = price;
        this.status = status;
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public UUID getTicketUid() { return ticketUid; }
    public void setTicketUid(UUID ticketUid) { this.ticketUid = ticketUid; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFlightNumber() { return flightNumber; }
    public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }

    public Integer getPrice() { return price; }
    public void setPrice(Integer price) { this.price = price; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}