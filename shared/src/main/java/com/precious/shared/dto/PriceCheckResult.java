package com.precious.shared.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

public class PriceCheckResult implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String assetName;
    private final BigDecimal currentPrice;
    private final BigDecimal targetPrice;
    private final boolean matches;
    private final String operation;
    private final Instant timestamp;
    private final String userEmail; // для отправки уведомления

    public PriceCheckResult(String assetName, BigDecimal currentPrice, BigDecimal targetPrice,
                            boolean matches, String operation, String userEmail) {
        this.assetName = Objects.requireNonNull(assetName);
        this.currentPrice = Objects.requireNonNull(currentPrice);
        this.targetPrice = Objects.requireNonNull(targetPrice);
        this.matches = Objects.requireNonNull(matches);
        this.operation = Objects.requireNonNull(operation);
        this.userEmail = Objects.requireNonNull(userEmail);
        this.timestamp = Objects.requireNonNull(Instant.now());
    }

    // Getters & Setters
    public String getAssetName() { return assetName; }

    public BigDecimal getCurrentPrice() { return currentPrice; }

    public BigDecimal getTargetPrice() { return targetPrice; }

    public boolean isMatches() { return matches; }

    public String getOperation() { return operation; }

    public Instant getTimestamp() { return timestamp; }

    public String getUserEmail() { return userEmail; }
}