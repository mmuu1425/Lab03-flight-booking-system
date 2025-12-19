// Gateway 项目中创建文件：src/main/java/com/example/gatewayservice/dto/TicketPurchaseResponse.java
package com.example.gatewayservice.dto;

import java.util.UUID;

public class TicketPurchaseResponse {
    private UUID ticketUid;
    private String flightNumber;
    private String fromAirport;
    private String toAirport;
    private String date;
    private Integer price;
    private Integer paidByMoney;
    private Integer paidByBonuses;
    private String status;
    private PrivilegeShortInfo privilege;

    // 必须有的getter/setter
    public UUID getTicketUid() { return ticketUid; }
    public void setTicketUid(UUID ticketUid) { this.ticketUid = ticketUid; }

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

    public Integer getPaidByMoney() { return paidByMoney; }
    public void setPaidByMoney(Integer paidByMoney) { this.paidByMoney = paidByMoney; }

    public Integer getPaidByBonuses() { return paidByBonuses; }
    public void setPaidByBonuses(Integer paidByBonuses) { this.paidByBonuses = paidByBonuses; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public PrivilegeShortInfo getPrivilege() { return privilege; }
    public void setPrivilege(PrivilegeShortInfo privilege) { this.privilege = privilege; }
}