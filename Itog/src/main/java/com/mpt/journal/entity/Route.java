package com.mpt.journal.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "routes")
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Аэропорт вылета обязателен")
    @ManyToOne
    @JoinColumn(name = "departure_airport_id", nullable = false)
    private Airport departureAirport;

    @NotNull(message = "Аэропорт прилёта обязателен")
    @ManyToOne
    @JoinColumn(name = "arrival_airport_id", nullable = false)
    private Airport arrivalAirport;

    @Min(value = 1, message = "Дистанция должна быть больше 0")
    @Column(name = "distance_km", nullable = false)
    private int distanceKm;

    public Route() {
    }

    // ==== геттеры / сеттеры ====

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Airport getDepartureAirport() {
        return departureAirport;
    }

    public void setDepartureAirport(Airport departureAirport) {
        this.departureAirport = departureAirport;
    }

    public Airport getArrivalAirport() {
        return arrivalAirport;
    }

    public void setArrivalAirport(Airport arrivalAirport) {
        this.arrivalAirport = arrivalAirport;
    }

    public int getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(int distanceKm) {
        this.distanceKm = distanceKm;
    }
}