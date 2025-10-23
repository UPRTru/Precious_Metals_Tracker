package controller;

import service.MetalPriceService;
import com.precious.shared.dto.PriceCheckResult;
import com.precious.shared.model.CurrentPrice;
import com.precious.shared.model.Metal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
public class PriceCheckRestController {

    private final MetalPriceService metalPriceService;

    public PriceCheckRestController(MetalPriceService metalPriceService) {
        this.metalPriceService = metalPriceService;
    }

    @GetMapping("/check")
    @Operation(summary = "Проверить текущую цену на металл")
    @ApiResponse(responseCode = "200", description = "Результат проверки", content = @Content(schema = @Schema(implementation = PriceCheckResult.class)))
    public PriceCheckResult check(
            @Parameter(description = "Название металла: GOLD, SILVER, PLATINUM") @RequestParam String metal,
            @Parameter(description = "Целевая цена") @RequestParam double target,
            @Parameter(description = "Операция: buy или sell") @RequestParam String operation
    ) {
        Metal m = Metal.valueOf(metal.toUpperCase());
        BigDecimal current = "buy".equals(operation)
                ? metalPriceService.getCurrentPrice(m, CurrentPrice.BUY)
                : metalPriceService.getCurrentPrice(m, CurrentPrice.SELL);
        boolean matches = "buy".equals(operation) ? current <= target : current >= target;
        return new PriceCheckResult(m.getDisplayName(), current, target, matches, operation, "test@example.com");
    }
}