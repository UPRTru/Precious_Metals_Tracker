package com.precious.general.service;

import com.precious.general.client.BankAgentClient;
import com.precious.shared.dto.CheckPrice;
import com.precious.shared.dto.Price;
import com.precious.shared.enums.Banks;
import com.precious.shared.enums.CurrentPrice;
import com.precious.shared.enums.TypePrice;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;

@Service
public class ServiceCheckPrice {

    private final BankAgentClient bankAgentClient;

    public ServiceCheckPrice(BankAgentClient bankAgentClient) {
        this.bankAgentClient = bankAgentClient;
    }

    public void checkPrice(String email, CheckPrice checkPrice) {
        Banks bank = checkPrice.bank();
        TypePrice typePrice = checkPrice.typePrice();
        CurrentPrice currentPrice = checkPrice.currentPrice();
        String name = checkPrice.name();
        BigDecimal price = checkPrice.price();
        Price newPrice = getNewPrice(typePrice, bank, name);

        if (priceComparison(currentPrice, price, newPrice.buyPrice(), newPrice.sellPrice())) {
            sendEmail(email);
        }
    }

    private Price getNewPrice(TypePrice typePrice, Banks bank, String name) {
        return switch (typePrice) {
            case METAL -> bankAgentClient.getLatestMetal(bank, name).block(Duration.ofSeconds(10));
            case CURRENCY -> bankAgentClient.getLatestCurrency(bank, name).block(Duration.ofSeconds(10));
            default -> throw new IllegalStateException("Unexpected value: " + typePrice);
        };
    }

    private boolean priceComparison(CurrentPrice currentPrice, BigDecimal price, BigDecimal buyPrice, BigDecimal sellPrice) {
        return switch (currentPrice) {
            case BUY -> buyPrice.compareTo(price) <= 0;
            case SELL -> sellPrice.compareTo(price) >= 0;
        };
    }

    private void sendEmail(String email) {

    }
}
