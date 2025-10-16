package com.precious.currency.controller;

import com.precious.currency.service.CurrencyPriceService;
import com.precious.shared.model.Currency;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.util.Map;

@Controller
public class CurrencyPriceController {

    private final CurrencyPriceService currencyPriceService;

    public CurrencyPriceController(CurrencyPriceService currencyPriceService) {
        this.currencyPriceService = currencyPriceService;
    }

    @GetMapping("/widget")
    public String widget() {
        return "widget";
    }

    @GetMapping("/history")
    @ResponseBody
    public Map<LocalDate, Double> getHistory(
            @RequestParam String currency,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        Currency currencyEnum = Currency.valueOf(currency.toUpperCase());
        return currencyPriceService.getHistory(currencyEnum, from, to);
    }
}