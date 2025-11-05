package com.precious.user.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

    @Column(name = "scheduler_prices")
    private List<SchedulerPrice> schedulerPrices;

    @Column(nullable = false)
    private String timezone;

    public User() {
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<SchedulerPrice> getSchedulerPrices() {
        return schedulerPrices;
    }

    public void addSchedulerPrice(SchedulerPrice schedulerPrice) {
        this.schedulerPrices.add(schedulerPrice);
    }

    public void setSchedulerPrices(List<SchedulerPrice> preferences) {
        this.schedulerPrices = schedulerPrices;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }
}