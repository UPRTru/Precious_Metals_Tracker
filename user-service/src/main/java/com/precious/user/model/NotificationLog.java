package com.precious.user.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "notification_log")
public class NotificationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userEmail;
    private String assetName;
    private double currentPrice;
    private double targetPrice;
    private String operation;
    private Instant sentAt = Instant.now();

    // Constructors
    public NotificationLog() {}

    public NotificationLog(String userEmail, String assetName, double currentPrice,
                           double targetPrice, String operation) {
        this.userEmail = userEmail;
        this.assetName = assetName;
        this.currentPrice = currentPrice;
        this.targetPrice = targetPrice;
        this.operation = operation;
    }

    // Getters
    public Long getId() { return id; }
    public String getUserEmail() { return userEmail; }
    public String getAssetName() { return assetName; }
    public double getCurrentPrice() { return currentPrice; }
    public double getTargetPrice() { return targetPrice; }
    public String getOperation() { return operation; }
    public Instant getSentAt() { return sentAt; }
}