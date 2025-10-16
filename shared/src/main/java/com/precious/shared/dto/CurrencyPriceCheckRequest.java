package com.precious.shared.dto;

import com.precious.shared.model.Currency;

import java.io.Serializable;
import java.util.Objects;

public class CurrencyPriceCheckRequest extends PriceCheckRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Currency currency;
    private final double targetPrice;
    private final String operation;
    private final String userEmail;

    public CurrencyPriceCheckRequest(Currency currency, double targetPrice, String operation, String userEmail) {
        this.currency = Objects.requireNonNull(currency);
        this.targetPrice = Objects.requireNonNull(targetPrice);
        this.operation = Objects.requireNonNull(operation);
        this.userEmail = Objects.requireNonNull(userEmail);
    }

    @Override
    public String getAssetName() {
        return currency.name();
    }

    public Currency getCurrency() {
        return currency;
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