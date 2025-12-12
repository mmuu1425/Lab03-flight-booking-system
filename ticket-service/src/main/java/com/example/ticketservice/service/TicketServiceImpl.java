package com.example.ticketservice.service;

import com.example.ticketservice.dto.TicketPurchaseResponse;
import com.example.ticketservice.dto.TicketResponse;
import com.example.ticketservice.dto.PrivilegeShortInfo;
import com.example.ticketservice.entity.Ticket;
import com.example.ticketservice.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final RestTemplate restTemplate;

    @Value("${service.bonus.url:http://bonus-service:8050}")
    private String bonusServiceBaseUrl;

    public TicketServiceImpl(TicketRepository ticketRepository, RestTemplate restTemplate) {
        this.ticketRepository = ticketRepository;
        this.restTemplate = restTemplate;
    }

    @Override
    public List<TicketResponse> getUserTickets(String username) {
        List<Ticket> tickets = ticketRepository.findByUsernameOrderByIdDesc(username);
        return tickets.stream()
                .map(this::convertToTicketResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<TicketResponse> getUserTicket(String username, UUID ticketUid) {
        Optional<Ticket> ticket = ticketRepository.findByTicketUidAndUsername(ticketUid, username);
        return ticket.map(this::convertToTicketResponse);
    }

    @Override
    @Transactional
    public TicketPurchaseResponse purchaseTicket(String username, String flightNumber, Integer price, Boolean paidFromBalance) {
        // 创建票
        Ticket ticket = new Ticket(username, flightNumber, price, "PAID");
        Ticket savedTicket = ticketRepository.save(ticket);

        // 调用Bonus Service处理积分
        BonusPurchaseRequest bonusRequest = new BonusPurchaseRequest();
        bonusRequest.setUsername(username);
        bonusRequest.setTicketUid(savedTicket.getTicketUid());
        bonusRequest.setPrice(price);
        bonusRequest.setPaidFromBalance(paidFromBalance != null ? paidFromBalance : false);

        String bonusServiceUrl = bonusServiceBaseUrl + "/api/v1/privilege/process-purchase";
        BonusOperationResponse bonusResponse = restTemplate.postForObject(bonusServiceUrl, bonusRequest, BonusOperationResponse.class);

        // 构建完整响应
        return buildPurchaseResponse(savedTicket, bonusResponse);
    }

    @Override
    @Transactional
    public boolean cancelTicket(String username, UUID ticketUid) {
        Optional<Ticket> ticketOpt = ticketRepository.findByTicketUidAndUsername(ticketUid, username);
        if (ticketOpt.isPresent()) {
            Ticket ticket = ticketOpt.get();
            ticket.setStatus("CANCELED");
            ticketRepository.save(ticket);

            // 调用Bonus Service处理退票积分
            BonusRefundRequest refundRequest = new BonusRefundRequest();
            refundRequest.setUsername(username);
            refundRequest.setTicketUid(ticketUid);

            String bonusServiceUrl = bonusServiceBaseUrl + "/api/v1/privilege/process-refund";
            restTemplate.postForObject(bonusServiceUrl, refundRequest, Void.class);

            return true;
        }
        return false;
    }

    @Override
    public boolean ticketExists(UUID ticketUid) {
        return ticketRepository.existsByTicketUid(ticketUid);
    }

    private TicketResponse convertToTicketResponse(Ticket ticket) {
        // 根据flightNumber设置对应的机场信息
        String fromAirport = "Санкт-Петербург Пулково";
        String toAirport = "Москва Шереметьево";
        String date = "2021-10-08 20:00";

        return new TicketResponse(
                ticket.getTicketUid(),
                ticket.getFlightNumber(),
                fromAirport,
                toAirport,
                date,
                ticket.getPrice(),
                ticket.getStatus()
        );
    }

    private TicketPurchaseResponse buildPurchaseResponse(Ticket ticket, BonusOperationResponse bonusResponse) {
        TicketPurchaseResponse response = new TicketPurchaseResponse();
        response.setTicketUid(ticket.getTicketUid());
        response.setFlightNumber(ticket.getFlightNumber());
        response.setFromAirport("Санкт-Петербург Пулково");
        response.setToAirport("Москва Шереметьево");
        response.setDate("2021-10-08 20:00");
        response.setPrice(ticket.getPrice());
        response.setStatus(ticket.getStatus());

        if (bonusResponse != null) {
            response.setPaidByMoney(bonusResponse.getPaidByMoney());
            response.setPaidByBonuses(bonusResponse.getPaidByBonuses());
            response.setPrivilege(bonusResponse.getPrivilege());
        } else {
            response.setPaidByMoney(ticket.getPrice());
            response.setPaidByBonuses(0);
            response.setPrivilege(new PrivilegeShortInfo(1500, "GOLD"));
        }

        return response;
    }

    // 内部请求类
    private static class BonusPurchaseRequest {
        private String username;
        private UUID ticketUid;
        private Integer price;
        private boolean paidFromBalance;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public UUID getTicketUid() { return ticketUid; }
        public void setTicketUid(UUID ticketUid) { this.ticketUid = ticketUid; }
        public Integer getPrice() { return price; }
        public void setPrice(Integer price) { this.price = price; }
        public boolean isPaidFromBalance() { return paidFromBalance; }
        public void setPaidFromBalance(boolean paidFromBalance) { this.paidFromBalance = paidFromBalance; }
    }

    private static class BonusRefundRequest {
        private String username;
        private UUID ticketUid;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public UUID getTicketUid() { return ticketUid; }
        public void setTicketUid(UUID ticketUid) { this.ticketUid = ticketUid; }
    }

    private static class BonusOperationResponse {
        private Integer paidByMoney;
        private Integer paidByBonuses;
        private PrivilegeShortInfo privilege;

        public Integer getPaidByMoney() { return paidByMoney; }
        public void setPaidByMoney(Integer paidByMoney) { this.paidByMoney = paidByMoney; }
        public Integer getPaidByBonuses() { return paidByBonuses; }
        public void setPaidByBonuses(Integer paidByBonuses) { this.paidByBonuses = paidByBonuses; }
        public PrivilegeShortInfo getPrivilege() { return privilege; }
        public void setPrivilege(PrivilegeShortInfo privilege) { this.privilege = privilege; }
    }
}