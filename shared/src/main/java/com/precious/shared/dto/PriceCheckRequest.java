package com.precious.shared.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = MetalPriceCheckRequest.class, name = "metal"),
        @JsonSubTypes.Type(value = CurrencyPriceCheckRequest.class, name = "currency")
})
public abstract class PriceCheckRequest {
    public abstract String getAssetName();
    public abstract double getTargetPrice();
    public abstract String getOperation(); // "buy" или "sell"
    public abstract String getUserEmail();
}