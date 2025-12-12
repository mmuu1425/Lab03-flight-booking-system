package com.example.flightservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
        "com.example.flightservice",
        "com.example.flightservice.controller",
        "com.example.flightservice.service",
        "com.example.flightservice.repository"
})
public class FlightServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(FlightServiceApplication.class, args);
    }
}