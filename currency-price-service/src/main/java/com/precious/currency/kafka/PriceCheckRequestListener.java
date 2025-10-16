package com.precious.currency.kafka;

import com.precious.currency.service.CurrencyPriceService;
import com.precious.shared.dto.CurrencyPriceCheckRequest;
import com.precious.shared.dto.EmailNotification;
import com.precious.shared.dto.PriceCheckResult;
import com.precious.shared.kafka.KafkaTopics;
import com.precious.shared.model.Currency;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PriceCheckRequestListener {

    private final CurrencyPriceService currencyPriceService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public PriceCheckRequestListener(CurrencyPriceService currencyPriceService,
                                     KafkaTemplate<String, Object> kafkaTemplate) {
        this.currencyPriceService = currencyPriceService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = KafkaTopics.PRICE_CHECK_REQUEST, groupId = "currency-group")
    public void handlePriceCheck(CurrencyPriceCheckRequest request) {
        Currency currency = request.getCurrency();
        double currentPrice = "buy".equals(request.getOperation())
                ? currencyPriceService.getCurrentBuyPrice(currency)
                : currencyPriceService.getCurrentSellPrice(currency);

        boolean matches = "buy".equals(request.getOperation())
                ? currentPrice <= request.getTargetPrice()
                : currentPrice >= request.getTargetPrice();

        String userEmail = request.getUserEmail(); // в реальности — из настроек пользователя

        PriceCheckResult result = new PriceCheckResult(
                currency.name(),
                currentPrice,
                request.getTargetPrice(),
                matches,
                request.getOperation(),
                userEmail
        );

        if (matches) {
            kafkaTemplate.send(KafkaTopics.NOTIFICATION_EMAIL,
            new EmailNotification(
                    userEmail,
                    "🔔 Уведомление о курсе " + currency.name(),
                    "<p>Текущий курс <b>" + currency.name() + "</b>: " +
                            currentPrice + " ₽</p>" +
                            "<p>Ваше условие: " + request.getOperation() + " при курсе " +
                            request.getTargetPrice() + " ₽ — <b>выполнено!</b></p>"
            ));
        }

        kafkaTemplate.send(KafkaTopics.PRICE_CHECK_RESULT, result);
    }
}