package com.example.flightservice.controller;

import com.example.flightservice.dto.FlightPaginationResponse;
import com.example.flightservice.entity.Flight;
import com.example.flightservice.service.FlightService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class FlightController {

    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    // 使用Service层获取航班列表
    @GetMapping("/flights")
    public ResponseEntity<FlightPaginationResponse> getFlights(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        System.out.println("=== [DEBUG] Flight Controller被调用 ===");
        System.out.println("=== [DEBUG] page: " + page + ", size: " + size + " ===");

        try {
            FlightPaginationResponse response = flightService.getFlights(page, size);

            System.out.println("=== [DEBUG] 返回数据: " + response.getItems().size() + " 个航班 ===");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("=== [DEBUG] 发生异常: " + e.getMessage() + " ===");
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    // 使用Service层获取特定航班
    @GetMapping("/flights/{flightNumber}")
    public ResponseEntity<Flight> getFlightByNumber(@PathVariable String flightNumber) {
        Optional<Flight> flight = flightService.getFlightByNumber(flightNumber);
        return flight.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 健康检查
    @GetMapping("/manage/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("OK");
    }
}