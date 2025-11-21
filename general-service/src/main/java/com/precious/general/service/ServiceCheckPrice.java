package com.precious.general.service;

import com.precious.general.client.BankAgentClient;
import com.precious.shared.model.Banks;
import com.precious.shared.model.CurrentPrice;
import com.precious.shared.model.JsonKeys;
import com.precious.shared.model.TypePrice;
import net.minidev.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class ServiceCheckPrice {

    private final BankAgentClient bankAgentClient;

    public ServiceCheckPrice(BankAgentClient bankAgentClient) {
        this.bankAgentClient = bankAgentClient;
    }

    public void checkPrice(String email, JSONObject jsonObject) {
        Banks bank = Banks.valueOf(jsonObject.getAsString(JsonKeys.BANK.getKey()));
        TypePrice typePrice = TypePrice.valueOf(jsonObject.getAsString(JsonKeys.TYPE_PRICE.getKey()));
        String name = jsonObject.getAsString(JsonKeys.NAME.getKey());
        CurrentPrice currentPrice = CurrentPrice.valueOf(jsonObject.getAsString(JsonKeys.CustomFields.PRICE.getKey()));
        double price = Double.parseDouble(jsonObject.getAsString(JsonKeys.CustomFields.PRICE.getKey()));

        JSONObject getObject = getDataPrice(typePrice, bank, name);

        double buyPrice = Double.parseDouble(getObject.getAsString(JsonKeys.BUY_PRICE.getKey()));
        double sellPrice = Double.parseDouble(getObject.getAsString(JsonKeys.SELL_PRICE.getKey()));

        if (priceComparison(price, buyPrice, sellPrice, currentPrice)) {
            sendEmail(email);
        }
    }

    private JSONObject getDataPrice(TypePrice typePrice, Banks bank, String name) {
        return switch (typePrice) {
            case METAL -> bankAgentClient.getLatestMetal(bank, name).blockFirst();
            case CURRENCY -> bankAgentClient.getLatestCurrency(bank, name).blockFirst();
            default -> throw new IllegalStateException("Unexpected value: " + typePrice);
        };
    }

    private boolean priceComparison(double price, double buyPrice, double sellPrice, CurrentPrice currentPrice) {
        return switch (currentPrice) {
            case BUY -> buyPrice <= price;
            case SELL -> sellPrice >= price;
            default -> throw new IllegalStateException("Unexpected value: " + currentPrice);
        };
    }

    private void sendEmail(String email) {

    }
}
