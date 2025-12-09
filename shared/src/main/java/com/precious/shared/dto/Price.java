package com.precious.shared.dto;

import java.math.BigDecimal;

public record Price(String bank, String name, BigDecimal buyPrice, BigDecimal sellPrice, long timestamp) {
}
