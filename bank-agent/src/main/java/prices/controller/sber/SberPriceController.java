package prices.controller.sber;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.web.bind.annotation.*;
import prices.repository.CurrencyPriceRepository;
import prices.repository.MetalPriceRepository;
import prices.utils.JsonUtils;

@RestController
@RequestMapping("/sber")
public class SberPriceController {

    private final MetalPriceRepository metalRepository;
    private final CurrencyPriceRepository currencyRepository;

    public SberPriceController(MetalPriceRepository metalRepository,
                               CurrencyPriceRepository currencyRepository) {
        this.metalRepository = metalRepository;
        this.currencyRepository = currencyRepository;
    }

    @GetMapping("/metal/lastprice/{metalName}")
    public JSONObject getSberLatestMetal(@PathVariable String metalName) {
        return metalRepository.findLatestByName(metalName).get().toJsonObject();
    }

    @GetMapping("/metal/all")
    public JSONArray getSberAllMetal() {
        return JsonUtils.mappingToJsonArray(metalRepository.findLatestUniqueByName());
    }

    @GetMapping("/metal/history/{metalName}")
    public JSONArray getHistoryMetal(
            @PathVariable String metalName,
            @RequestParam Long from,
            @RequestParam Long to) {
        return JsonUtils.mappingToJsonArray(metalRepository.findByNameAndTimestampBetweenOrderByTimestampAsc(metalName, from, to));
    }

    @GetMapping("/currency/lastprice/{currencyName}")
    public JSONObject getSberLatestCurrency(@PathVariable String currencyName) {
        return currencyRepository.findLatestByName(currencyName).get().toJsonObject();
    }

    @GetMapping("/currency/all")
    public JSONArray getSberAllCurrency() {
        return JsonUtils.mappingToJsonArray(currencyRepository.findLatestUniqueByName());
    }

    @GetMapping("/currency/history/{currencyName}")
    public JSONArray getHistoryCurrency(
            @PathVariable String currencyName,
            @RequestParam Long from,
            @RequestParam Long to) {
        return JsonUtils.mappingToJsonArray(currencyRepository.findByNameAndTimestampBetweenOrderByTimestampAsc(currencyName, from, to));
    }
}