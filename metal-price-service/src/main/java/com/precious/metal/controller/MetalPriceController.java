package com.precious.metal.controller;

import com.precious.metal.service.MetalPriceService;
import com.precious.shared.model.Metal;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.util.Map;

@Controller
public class MetalPriceController {

    private final MetalPriceService metalPriceService;

    public MetalPriceController(MetalPriceService metalPriceService) {
        this.metalPriceService = metalPriceService;
    }

    @GetMapping("/widget")
    public String widget() {
        return "widget";
    }

    @GetMapping("/history")
    @ResponseBody
    public Map<LocalDate, Double> getHistory(
            @RequestParam String metal,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        Metal metalEnum = Metal.valueOf(metal.toUpperCase());
        return metalPriceService.getHistory(metalEnum, from, to);
    }
}