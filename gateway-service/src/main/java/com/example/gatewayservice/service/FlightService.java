package com.example.gatewayservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class FlightService {

    private final RestTemplate restTemplate;

    @Value("${service.flight.url}")
    private String flightServiceUrl;

    public FlightService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<Object> getFlights(int page, int size) {
        try {
            String url = flightServiceUrl + "/api/v1/flights?page=" + page + "&size=" + size;
            Object flights = restTemplate.getForObject(url, Object.class);
            return ResponseEntity.ok(flights);
        } catch (Exception e) {
            return ResponseEntity.status(503).body(Map.of(
                    "message", "Flight service is unavailable",
                    "error", "Service Unavailable"
            ));
        }
    }
}