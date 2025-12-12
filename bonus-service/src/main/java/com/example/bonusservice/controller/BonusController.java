package com.example.bonusservice.controller;

import com.example.bonusservice.dto.*;
import com.example.bonusservice.entity.Privilege;
import com.example.bonusservice.entity.PrivilegeHistory;
import com.example.bonusservice.service.BonusService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/privilege")
public class BonusController {

    private final BonusService bonusService;

    public BonusController(BonusService bonusService) {
        this.bonusService = bonusService;
    }

    // 获取用户特权信息（包含历史）
    @GetMapping
    public ResponseEntity<PrivilegeResponse> getPrivilegeInfo(
            @RequestHeader("X-User-Name") String username) {

        Privilege privilege = bonusService.getOrCreatePrivilege(username);
        List<PrivilegeHistory> history = bonusService.getPrivilegeHistory(username);

        // 转换为Postman期望的DTO格式
        PrivilegeResponse response = convertToPrivilegeResponse(privilege, history);
        return ResponseEntity.ok(response);
    }

    // 简单测试端点
    @GetMapping("/simple")
    public ResponseEntity<PrivilegeResponse> getSimplePrivilege(@RequestHeader("X-User-Name") String username) {
        // 创建符合Postman格式的简单响应
        PrivilegeResponse response = new PrivilegeResponse();
        response.setBalance(1500);
        response.setStatus("GOLD");
        response.setHistory(List.of());

        return ResponseEntity.ok(response);
    }

    // 处理购买积分操作（内部调用）
    @PostMapping("/process-purchase")
    public ResponseEntity<BonusOperationResponse> processPurchase(
            @RequestBody PurchaseRequest request) {

        BonusService.BonusOperationResult result = bonusService.processPurchase(
                request.getUsername(),
                request.getTicketUid(),
                request.getPrice(),
                request.isPaidFromBalance()
        );

        // 转换为DTO
        PrivilegeShortInfo privilegeInfo = new PrivilegeShortInfo(
                result.getPrivilege().getBalance(),
                result.getPrivilege().getStatus()
        );

        BonusOperationResponse response = new BonusOperationResponse(
                result.getPaidByMoney(),
                result.getPaidByBonuses(),
                privilegeInfo
        );

        return ResponseEntity.ok(response);
    }

    // 处理退票积分操作（内部调用）
    @PostMapping("/process-refund")
    public ResponseEntity<Void> processRefund(@RequestBody RefundRequest request) {
        bonusService.processRefund(request.getUsername(), request.getTicketUid());
        return ResponseEntity.ok().build();
    }

    // 测试端点
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Bonus Service is working!");
    }

    // 健康检查
    @GetMapping("/manage/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("OK");
    }

    // 转换实体到DTO
    private PrivilegeResponse convertToPrivilegeResponse(Privilege privilege, List<PrivilegeHistory> history) {
        PrivilegeResponse response = new PrivilegeResponse();
        response.setBalance(privilege.getBalance());
        response.setStatus(privilege.getStatus());

        // 转换历史记录
        List<PrivilegeHistoryResponse> historyResponses = history.stream()
                .map(this::convertToHistoryResponse)
                .collect(Collectors.toList());
        response.setHistory(historyResponses);

        return response;
    }

    private PrivilegeHistoryResponse convertToHistoryResponse(PrivilegeHistory history) {
        PrivilegeHistoryResponse response = new PrivilegeHistoryResponse();
        response.setDate(history.getDatetime()); // 会自动格式化为ISO格式
        response.setTicketUid(history.getTicketUid().toString());
        response.setBalanceDiff(history.getBalanceDiff());
        response.setOperationType(history.getOperationType());
        return response;
    }
}