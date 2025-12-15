package com.precious.user.controller;

import com.precious.user.client.GeneralServiceClient;
import com.precious.user.service.UserService;
import com.precious.user.utils.ZonedDateTimeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

public class DashboardController {

    private final UserService userService;
    private final GeneralServiceClient generalServiceClient;

    public DashboardController(UserService userService, GeneralServiceClient generalServiceClient) {
        this.userService = userService;
        this.generalServiceClient = generalServiceClient;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/health")
    @ResponseBody
    public String health() {
        return "OK";
    }

    @GetMapping("/api/users/summary")
    @ResponseBody
    public Map<String, Object> userSummary() {
        Long lustUpdatePrices = generalServiceClient.getLustUpdatePrices();
        String userZoneDateTime = userService.getUserZoneDateTime();
        String lastUpdate = ZonedDateTimeUtils.getUserTime(lustUpdatePrices, userZoneDateTime);
        String schedulePrices = userService.getAllScheduledPrices().toString();
        return Map.of(
                "schedulePrices", schedulePrices,
                "lastUpdate", lastUpdate
        );

        //todo обновление с фронта каждые ... сек
    }
}
