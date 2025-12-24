package com.precious.general.service.mail;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ServiceEmail {

    public ServiceEmail() {}

    public record Message(String name, BigDecimal buyPrice, BigDecimal sellPrice) {}

    public void sendEmail(String email, Message message) {

    }

    private StringBuilder createTextMessage(Message message) {
        StringBuilder text = new StringBuilder();
        text.append("Name: ").append(message.name()).append("\n");
        text.append("Buy price: ").append(message.buyPrice()).append("\n");
        text.append("Sell price: ").append(message.sellPrice()).append("\n");
        return text;
    }
}
