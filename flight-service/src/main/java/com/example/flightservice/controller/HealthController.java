package com.example.flightservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
    // 统一的健康检查（根路径）
    @GetMapping("/manage/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("OK");
    }

}
