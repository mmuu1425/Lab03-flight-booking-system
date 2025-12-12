package com.example.ticketservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/manage/health")
    public String healthCheck() {
        return "OK";
    }
}