package com.precious.user.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

public class DashboardController {
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
        //todo Замените на реальный запрос к БД
        return Map.of(
                "totalUsers", 142,
                "lastLogin", System.currentTimeMillis() - 3600000 // 1 час назад
        );
    }
}
