package com.precious.metal.kafka;

import com.precious.metal.service.MetalPriceService;
import com.precious.shared.dto.MetalPriceCheckRequest;
import com.precious.shared.dto.PriceCheckResult;
import com.precious.shared.kafka.KafkaTopics;
import com.precious.shared.model.CurrentPrice;
import com.precious.shared.model.Metal;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PriceCheckRequestListener {

    private final MetalPriceService metalPriceService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public PriceCheckRequestListener(MetalPriceService metalPriceService,
                                     KafkaTemplate<String, Object> kafkaTemplate) {
        this.metalPriceService = metalPriceService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = KafkaTopics.PRICE_CHECK_REQUEST, groupId = "metal-group")
    public void handlePriceCheck(MetalPriceCheckRequest request) {
        Metal metal = request.getMetal();
        BigDecimal currentPrice = CurrentPrice.BUY.name().equals(request.getOperation())
                ? metalPriceService.getLatestPrice(metal.getDisplayName()).getBuyPrice()
                : metalPriceService.getLatestPrice(metal.getDisplayName()).getSellPrice();

        boolean matches = CurrentPrice.BUY.name().equals(request.getOperation())
                ? currentPrice <= request.getTargetPrice()   // покупаем дешевле
                : currentPrice >= request.getTargetPrice();  // продаём дороже

        // В реальном приложении email брался бы из настроек пользователя,
        // но для демо — заглушка
        String userEmail = request.getUserEmail();

        PriceCheckResult result = new PriceCheckResult(
                metal.getDisplayName(),
                currentPrice,
                request.getTargetPrice(),
                matches,
                request.getOperation(),
                userEmail
        );

        if (matches) {
            kafkaTemplate.send(KafkaTopics.NOTIFICATION_EMAIL, new com.precious.shared.dto.EmailNotification(
                    userEmail,
                    "🔔 Уведомление о цене на " + metal.getDisplayName(),
                    "<p>Текущая цена на <b>" + metal.getDisplayName() + "</b>: " +
                            currentPrice + " ₽</p>" +
                            "<p>Ваше условие: " + request.getOperation() + " при цене " +
                            request.getTargetPrice() + " ₽ — <b>выполнено!</b></p>"
            ));
        }

        // Отправляем результат (можно логировать или использовать в gateway)
        kafkaTemplate.send(KafkaTopics.PRICE_CHECK_RESULT, result);
    }
}