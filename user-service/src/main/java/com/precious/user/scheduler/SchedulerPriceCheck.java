package com.precious.user.scheduler;

import com.precious.user.client.GeneralServiceClient;
import com.precious.user.model.User;
import com.precious.user.service.UserService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SchedulerPriceCheck {

    private final GeneralServiceClient generalServiceClient;
    private final UserService userService;

    public SchedulerPriceCheck(GeneralServiceClient generalServiceClient,
                               UserService userService) {
        this.generalServiceClient = generalServiceClient;
        this.userService = userService;
    }

    @Scheduled(fixedRate = 300_000) // 5 минут
    public void checkPrices() {
        try {
            List<User> users = userService.getAllUsers();
            for (User user : users) {
                user.getScheduledPrices()
                        .forEach(
                        scheduledPrice -> generalServiceClient.checkScheduledPrice(user.getEmail(), scheduledPrice.getCheckPrice())
                        );
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}