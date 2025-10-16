package com.precious.currency.controller;

import com.precious.currency.service.CurrencyPriceService;
import com.precious.shared.dto.PriceCheckResult;
import com.precious.shared.model.Currency;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
public class PriceCheckRestController {

    private final CurrencyPriceService currencyPriceService;

    public PriceCheckRestController(CurrencyPriceService currencyPriceService) {
        this.currencyPriceService = currencyPriceService;
    }

    @GetMapping("/check")
//    public PriceCheckResult check(
//            @RequestParam String currency,
//            @RequestParam double target,
//            @RequestParam String operation
//    )
    @Operation(summary = "Проверить текущую цену на металл")
    @ApiResponse(responseCode = "200", description = "Результат проверки", content = @Content(schema = @Schema(implementation = PriceCheckResult.class)))
    public PriceCheckResult check(
            @Parameter(description = "Название валюты: USD, EUR, GBP, JPY") @RequestParam String currency,
            @Parameter(description = "Целевая цена") @RequestParam double target,
            @Parameter(description = "Операция: buy или sell") @RequestParam String operation
    ) {
        Currency c = Currency.valueOf(currency.toUpperCase());
        double current = "buy".equals(operation)
                ? currencyPriceService.getCurrentBuyPrice(c)
                : currencyPriceService.getCurrentSellPrice(c);
        boolean matches = "buy".equals(operation) ? current <= target : current >= target;
        return new PriceCheckResult(c.name(), current, target, matches, operation, "test@example.com");
    }
}