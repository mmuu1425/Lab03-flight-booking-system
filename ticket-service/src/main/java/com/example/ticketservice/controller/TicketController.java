package com.example.ticketservice.controller;

import com.example.ticketservice.dto.TicketPurchaseRequest;
import com.example.ticketservice.dto.TicketPurchaseResponse;
import com.example.ticketservice.dto.TicketResponse;
import com.example.ticketservice.service.TicketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    // 获取用户所有票 - 返回包含机场信息的DTO
    @GetMapping
    public ResponseEntity<List<TicketResponse>> getUserTickets(@RequestHeader("X-User-Name") String username) {
        List<TicketResponse> tickets = ticketService.getUserTickets(username);
        return ResponseEntity.ok(tickets);
    }

    // 获取特定票信息 - 返回包含机场信息的DTO
    @GetMapping("/{ticketUid}")
    public ResponseEntity<TicketResponse> getTicket(
            @PathVariable("ticketUid") UUID ticketUid,
            @RequestHeader("X-User-Name") String username) {

        return ticketService.getUserTicket(username, ticketUid)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 购买票 - 返回完整的购买响应
    @PostMapping
    public ResponseEntity<TicketPurchaseResponse> purchaseTicket(
            @RequestBody TicketPurchaseRequest request,
            @RequestHeader("X-User-Name") String username) {

        TicketPurchaseResponse response = ticketService.purchaseTicket(
                username,
                request.getFlightNumber(),
                request.getPrice(),
                request.getPaidFromBalance()
        );
        return ResponseEntity.ok(response);
    }

    // 退票
    @DeleteMapping("/{ticketUid}")
    public ResponseEntity<Void> cancelTicket(
            @PathVariable("ticketUid") UUID ticketUid,
            @RequestHeader("X-User-Name") String username) {

        boolean canceled = ticketService.cancelTicket(username, ticketUid);
        return canceled ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    // 健康检查
    @GetMapping("/manage/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("OK");
    }
}