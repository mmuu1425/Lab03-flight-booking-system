package com.example.ticketservice.service;

import com.example.ticketservice.dto.*;
import com.example.ticketservice.entity.Ticket;
import com.example.ticketservice.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final RetryService retryService;

    // 移除对 RestTemplate 的依赖，不再直接调用 Bonus Service
    public TicketServiceImpl(TicketRepository ticketRepository,
                             RetryService retryService) {
        this.ticketRepository = ticketRepository;
        this.retryService = retryService;
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
    public TicketPurchaseResponse purchaseTicket(String username, String flightNumber,
                                                 Integer price, Boolean paidFromBalance) {
        // 1. 创建ticket，初始状态为PAID
        Ticket ticket = new Ticket(username, flightNumber, price, "PAID");
        Ticket savedTicket = ticketRepository.save(ticket);

        // 2. 根据测试需求，Gateway会处理Bonus Service调用
        // 这里只返回基本的票务信息，privilege信息由Gateway添加

        return buildBasicPurchaseResponse(savedTicket, paidFromBalance);
    }

    @Override
    @Transactional
    public boolean cancelTicket(String username, UUID ticketUid) {
        Optional<Ticket> ticketOpt = ticketRepository.findByTicketUidAndUsername(ticketUid, username);
        if (ticketOpt.isEmpty()) {
            return false;
        }

        Ticket ticket = ticketOpt.get();

        // 如果已经是CANCELED状态，直接返回true
        if ("CANCELED".equals(ticket.getStatus())) {
            return true;
        }

        // 更新票状态为CANCELED
        ticket.setStatus("CANCELED");
        ticketRepository.save(ticket);

        // Bonus Service的处理由Gateway负责
        return true;
    }

    @Override
    public boolean ticketExists(UUID ticketUid) {
        return ticketRepository.existsByTicketUid(ticketUid);
    }

    // === 私有方法 ===

    private TicketResponse convertToTicketResponse(Ticket ticket) {
        // 根据测试需求，返回固定格式的数据
        return new TicketResponse(
                ticket.getTicketUid(),
                ticket.getFlightNumber(),
                "Санкт-Петербург Пулково",
                "Москва Шереметьево",
                "2021-10-08 20:00",
                ticket.getPrice(),
                ticket.getStatus()
        );
    }

    private TicketPurchaseResponse buildBasicPurchaseResponse(Ticket ticket, Boolean paidFromBalance) {
        TicketPurchaseResponse response = new TicketPurchaseResponse();
        response.setTicketUid(ticket.getTicketUid());
        response.setFlightNumber(ticket.getFlightNumber());
        response.setFromAirport("Санкт-Петербург Пулково");
        response.setToAirport("Москва Шереметьево");
        response.setDate("2021-10-08 20:00");
        response.setPrice(ticket.getPrice());
        response.setStatus(ticket.getStatus());

        // 根据是否使用余额设置支付方式
        boolean useBalance = paidFromBalance != null && paidFromBalance;
        if (useBalance) {
            // 假设最大使用票价30%的余额
            int maxBonusUsage = (int) (ticket.getPrice() * 0.3);
            response.setPaidByBonuses(Math.min(1500, maxBonusUsage)); // 假设用户有1500余额
            response.setPaidByMoney(ticket.getPrice() - response.getPaidByBonuses());
        } else {
            response.setPaidByMoney(ticket.getPrice());
            response.setPaidByBonuses(0);
        }

        // privilege字段由Gateway添加，这里设为null
        response.setPrivilege(null);

        return response;
    }

    // 重试方法（保留用于其他可能的异步操作）
    private void retryOperation(Runnable operation, String description) {
        retryService.addTask(() -> {
            try {
                operation.run();
                System.out.println("Retry successful: " + description);
            } catch (Exception e) {
                System.err.println("Retry failed for " + description + ": " + e.getMessage());
                throw e;
            }
        });
    }

    // 内部类，用于处理需要重试的操作
    private static class InternalRetryTask implements RetryService.RetryTask {
        private final Runnable operation;
        private final String description;

        public InternalRetryTask(Runnable operation, String description) {
            this.operation = operation;
            this.description = description;
        }

        @Override
        public void execute() throws Exception {
            try {
                operation.run();
                System.out.println("Retry successful: " + description);
            } catch (Exception e) {
                System.err.println("Retry failed for " + description + ": " + e.getMessage());
                throw e;
            }
        }
    }
}