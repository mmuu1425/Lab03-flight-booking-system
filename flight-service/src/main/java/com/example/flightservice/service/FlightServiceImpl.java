package com.example.flightservice.service;

import com.example.flightservice.dto.FlightItemResponse;
import com.example.flightservice.dto.FlightPaginationResponse;
import com.example.flightservice.entity.Flight;
import com.example.flightservice.repository.FlightRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FlightServiceImpl implements FlightService {

    private final FlightRepository flightRepository;

    public FlightServiceImpl(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    @Override
    public FlightPaginationResponse getFlights(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Flight> flightPage = flightRepository.findAll(pageable);

        List<FlightItemResponse> items = flightPage.getContent().stream()
                .map(this::convertToFlightItem)
                .collect(Collectors.toList());

        return new FlightPaginationResponse(
                page,
                size,
                (int) flightPage.getTotalElements(),
                items
        );
    }

    @Override
    public Optional<Flight> getFlightByNumber(String flightNumber) {
        return flightRepository.findByFlightNumber(flightNumber);
    }

    @Override
    public List<FlightItemResponse> getAllFlights() {
        return flightRepository.findAll().stream()
                .map(this::convertToFlightItem)
                .collect(Collectors.toList());
    }

    private FlightItemResponse convertToFlightItem(Flight flight) {
        FlightItemResponse item = new FlightItemResponse();
        item.setFlightNumber(flight.getFlightNumber());

        // 构建机场显示字符串
        if (flight.getFromAirport() != null) {
            String fromAirport = flight.getFromAirport().getCity() + " " + flight.getFromAirport().getName();
            item.setFromAirport(fromAirport);
        }

        if (flight.getToAirport() != null) {
            String toAirport = flight.getToAirport().getCity() + " " + flight.getToAirport().getName();
            item.setToAirport(toAirport);
        }

        // 格式化日期
        if (flight.getDatetime() != null) {
            String formattedDate = flight.getDatetime().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            item.setDate(formattedDate);
        }

        item.setPrice(flight.getPrice());
        return item;
    }
}