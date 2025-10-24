package com.precious.shared.dto;

import com.precious.shared.model.Metal;

import java.io.Serializable;
import java.util.Objects;

public class MetalPriceCheckRequest extends PriceCheckRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Metal metal;
    private final double targetPrice;
    private final String operation;
    private final String userEmail;

    public MetalPriceCheckRequest(Metal metal, double targetPrice, String operation, String userEmail) {
        this.metal = Objects.requireNonNull(metal);
        this.targetPrice = targetPrice;
        this.operation = Objects.requireNonNull(operation);
        this.userEmail = Objects.requireNonNull(userEmail);
    }

    @Override
    public String getAssetName() {
        return metal.getDisplayName();
    }

    public Metal getMetal() {
        return metal;
    }

    @Override
    public double getTargetPrice() {
        return targetPrice;
    }

    @Override
    public String getOperation() {
        return operation;
    }

    public String getUserEmail() {
        return userEmail;
    }
}