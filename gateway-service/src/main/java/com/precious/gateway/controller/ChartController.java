package com.precious.gateway.controller;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Controller
public class ChartController {

    private final WebClient metalClient;
    private final WebClient currencyClient;

    public ChartController() {
        this.metalClient = WebClient.create("http://localhost:8082");
        this.currencyClient = WebClient.create("http://localhost:8083");
    }

    @GetMapping("/admin/charts")
    public String showChart(
            @RequestParam(defaultValue = "GOLD") String asset,
            @RequestParam(defaultValue = "metal") String type,
            Model model) {

        LocalDate to = LocalDate.now();
        LocalDate from = to.minusDays(30);
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

        ParameterizedTypeReference<Map<String, Double>> responseType =
                new ParameterizedTypeReference<>() {
                };

        Map<String, Double> data = ("metal".equals(type)
                ? metalClient.get()
                .uri("/history?metal=" + asset + "&from=" + from.format(formatter) + "&to=" + to.format(formatter))
                .retrieve().bodyToMono(responseType)
                : currencyClient.get()
                .uri("/history?currency=" + asset + "&from=" + from.format(formatter) + "&to=" + to.format(formatter))
                .retrieve().bodyToMono(responseType)
        ).block();

        if (data != null) {
            model.addAttribute("dates", data.keySet().stream().toList());
            model.addAttribute("prices", data.values().stream().map(String::valueOf).toList());
        } else {
            model.addAttribute("dates", List.of());
            model.addAttribute("prices", List.of());
        }

        model.addAttribute("asset", asset);
        model.addAttribute("type", type);
        return "admin/chart";
    }
}