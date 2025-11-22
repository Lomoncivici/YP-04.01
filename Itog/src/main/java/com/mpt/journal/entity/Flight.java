package com.mpt.journal.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "flights")
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Номер рейса обязателен")
    @Column(name = "flight_number", nullable = false, unique = true, length = 10)
    private String flightNumber;

    @NotNull(message = "Маршрут обязателен")
    @ManyToOne
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @NotNull(message = "Самолёт обязателен")
    @ManyToOne
    @JoinColumn(name = "aircraft_id", nullable = false)
    private Aircraft aircraft;

    @NotNull(message = "Время вылета обязательно")
    @Column(name = "departure_time", nullable = false)
    private LocalDateTime departureTime;

    @NotNull(message = "Время прилёта обязательно")
    @Column(name = "arrival_time", nullable = false)
    private LocalDateTime arrivalTime;

    @NotNull(message = "Базовая цена обязательна")
    @Min(value = 1, message = "Цена должна быть больше 0")
    @Column(name = "base_price", nullable = false)
    private BigDecimal basePrice;

    @NotBlank(message = "Статус обязателен")
    @Column(name = "status", nullable = false)
    private String status;

    public Flight() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public Aircraft getAircraft() {
        return aircraft;
    }

    public void setAircraft(Aircraft aircraft) {
        this.aircraft = aircraft;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
