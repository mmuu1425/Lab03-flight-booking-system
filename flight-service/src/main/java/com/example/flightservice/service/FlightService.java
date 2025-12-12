package com.example.flightservice.service;

import com.example.flightservice.dto.FlightItemResponse;
import com.example.flightservice.dto.FlightPaginationResponse;
import com.example.flightservice.entity.Flight;

import java.util.List;
import java.util.Optional;

public interface FlightService {
    FlightPaginationResponse getFlights(int page, int size);
    Optional<Flight> getFlightByNumber(String flightNumber);
    List<FlightItemResponse> getAllFlights();
}