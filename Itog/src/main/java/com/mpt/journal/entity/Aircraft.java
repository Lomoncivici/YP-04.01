package com.mpt.journal.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "aircrafts")
public class Aircraft {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Модель самолёта обязательна")
    @Column(name = "model", nullable = false)
    private String model;

    @Min(value = 1, message = "Общее количество мест должно быть больше 0")
    @Column(name = "total_seats", nullable = false)
    private int totalSeats;

    @Min(value = 0, message = "Количество мест эконом-класса не может быть меньше 0")
    @Column(name = "economy_seats", nullable = false)
    private int economySeats;

    @Min(value = 0, message = "Количество мест бизнес-класса не может быть меньше 0")
    @Column(name = "business_seats", nullable = false)
    private int businessSeats;

    public Aircraft() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public int getEconomySeats() {
        return economySeats;
    }

    public void setEconomySeats(int economySeats) {
        this.economySeats = economySeats;
    }

    public int getBusinessSeats() {
        return businessSeats;
    }

    public void setBusinessSeats(int businessSeats) {
        this.businessSeats = businessSeats;
    }
}
