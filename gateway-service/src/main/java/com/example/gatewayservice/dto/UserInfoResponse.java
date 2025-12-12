package com.example.gatewayservice.dto;

import java.util.List;

public class UserInfoResponse {
    private List<TicketResponse> tickets;
    private PrivilegeShortInfo privilege;

    public UserInfoResponse() {}

    public UserInfoResponse(List<TicketResponse> tickets, PrivilegeShortInfo privilege) {
        this.tickets = tickets;
        this.privilege = privilege;
    }

    // Getters and Setters
    public List<TicketResponse> getTickets() { return tickets; }
    public void setTickets(List<TicketResponse> tickets) { this.tickets = tickets; }

    public PrivilegeShortInfo getPrivilege() { return privilege; }
    public void setPrivilege(PrivilegeShortInfo privilege) { this.privilege = privilege; }
}