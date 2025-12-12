package com.example.gatewayservice.controller;

import com.example.gatewayservice.dto.*;
import com.example.gatewayservice.service.GatewayService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1")
public class GatewayController {

    private final GatewayService gatewayService;

    public GatewayController(GatewayService gatewayService) {
        this.gatewayService = gatewayService;
    }

    // 获取用户完整信息
    @GetMapping("/me")
    public Mono<ResponseEntity<UserInfoResponse>> getUserInfo(
            @RequestHeader("X-User-Name") String username) {

        if (username == null || username.trim().isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().build());
        }

        try {
            UserInfoResponse userInfo = gatewayService.getUserInfo(username);
            return Mono.just(ResponseEntity.ok(userInfo));
        } catch (Exception e) {
            return Mono.just(ResponseEntity.status(503).build());
        }
    }

    // 获取特权信息
    @GetMapping("/privilege")
    public Mono<ResponseEntity<PrivilegeResponse>> getPrivilege(
            @RequestHeader("X-User-Name") String username) {

        if (username == null || username.trim().isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().build());
        }

        try {
            PrivilegeResponse privilege = gatewayService.getPrivilegeInfo(username);
            return Mono.just(ResponseEntity.ok(privilege));
        } catch (Exception e) {
            return Mono.just(ResponseEntity.status(503).build());
        }
    }

    // 获取用户所有票
    @GetMapping("/tickets")
    public Mono<ResponseEntity<?>> getUserTickets(
            @RequestHeader("X-User-Name") String username) {

        if (username == null || username.trim().isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().build());
        }

        try {
            Object tickets = gatewayService.getUserTickets(username);
            return Mono.just(ResponseEntity.ok(tickets));
        } catch (Exception e) {
            return Mono.just(ResponseEntity.status(503).build());
        }
    }

    // 获取特定票 - 修复路径参数
    @GetMapping("/tickets/{ticketUid}")
    public Mono<ResponseEntity<?>> getTicket(
            @PathVariable("ticketUid") String ticketUid,
            @RequestHeader("X-User-Name") String username) {

        if (username == null || username.trim().isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().build());
        }

        try {
            Object ticket = gatewayService.getUserTicket(username, ticketUid);
            return Mono.just(ResponseEntity.ok(ticket));
        } catch (Exception e) {
            return Mono.just(ResponseEntity.status(503).build());
        }
    }

    // 购买票
    @PostMapping("/tickets")
    public Mono<ResponseEntity<?>> purchaseTicket(
            @RequestHeader("X-User-Name") String username,
            @RequestBody Object request) {

        if (username == null || username.trim().isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().build());
        }

        try {
            Object response = gatewayService.purchaseTicket(username, request);
            return Mono.just(ResponseEntity.ok(response));
        } catch (Exception e) {
            return Mono.just(ResponseEntity.status(503).build());
        }
    }

    // 退票 - 修复路径参数
    @DeleteMapping("/tickets/{ticketUid}")
    public Mono<ResponseEntity<Void>> returnTicket(
            @PathVariable("ticketUid") String ticketUid,
            @RequestHeader("X-User-Name") String username) {

        if (username == null || username.trim().isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().build());
        }

        try {
            boolean success = gatewayService.returnTicket(username, ticketUid);
            if (success) {
                return Mono.just(ResponseEntity.noContent().build());
            } else {
                return Mono.just(ResponseEntity.notFound().build());
            }
        } catch (Exception e) {
            return Mono.just(ResponseEntity.status(503).build());
        }
    }

    // 获取航班列表
    @GetMapping("/flights")
    public Mono<ResponseEntity<?>> getFlights(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {

        try {
            Object flights = gatewayService.getFlights(page, size);
            return Mono.just(ResponseEntity.ok(flights));
        } catch (Exception e) {
            return Mono.just(ResponseEntity.status(503).build());
        }
    }

    // 健康检查
    @GetMapping("/manage/health")
    public Mono<ResponseEntity<String>> healthCheck() {
        return Mono.just(ResponseEntity.ok("OK"));
    }
}