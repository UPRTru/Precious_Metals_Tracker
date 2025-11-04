package prices.controller.sber;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class PriceCheckRestController {

    private final CurrencyPriceService currencyPriceService;

    public PriceCheckRestController(CurrencyPriceService currencyPriceService) {
        this.currencyPriceService = currencyPriceService;
    }

//    @GetMapping("/check")
//    @Operation(summary = "Проверить текущую цену на металл")
//    @ApiResponse(responseCode = "200", description = "Результат проверки", content = @Content(schema = @Schema(implementation = PriceCheckResult.class)))
//    public PriceCheckResult check(
//            @Parameter(description = "Название металла: GOLD, SILVER, PLATINUM") @RequestParam String metal,
//            @Parameter(description = "Целевая цена") @RequestParam double target,
//            @Parameter(description = "Операция: buy или sell") @RequestParam CurrentPrice operation
//    ) {
//        Metal m = Metal.valueOf(metal.toUpperCase());
//        BigDecimal current = "buy".equals(operation)
//                ? priceService.getCurrentPrice(m, CurrentPrice.BUY)
//                : priceService.getCurrentPrice(m, CurrentPrice.SELL);
//        boolean matches = "buy".equals(operation) ? current <= target : current >= target;
//        return new PriceCheckResult(m.getDisplayName(), current, target, matches, operation, "test@example.com");
//    }
}