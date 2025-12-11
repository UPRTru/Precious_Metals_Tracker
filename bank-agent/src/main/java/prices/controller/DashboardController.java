package prices.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
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

    @GetMapping("/api/bank/latest")
    @ResponseBody
    public Map<String, Object> latestRates() {
        //замените на логику получения данных
        List<Map<String, Object>> rates = List.of(
                Map.of("name", "Золото", "value", 6500.45, "unit", "RUB/гр"),
                Map.of("name", "Серебро", "value", 85.20, "unit", "RUB/гр"),
                Map.of("name", "USD", "value", 92.35, "unit", "RUB")
        );
        return Map.of("rates", rates);
    }
}
