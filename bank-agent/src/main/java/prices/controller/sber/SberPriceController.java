package prices.controller.sber;

import net.minidev.json.JSONObject;
import org.springframework.web.bind.annotation.*;
import prices.model.CurrencyPrice;
import prices.model.MetalPrice;
import prices.repository.MetalPriceRepository;
import prices.repository.CurrencyPriceRepository;

import java.util.List;

@RestController
@RequestMapping("/sber/metal")
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
    public List<MetalPrice> getSberAllMetal() {
        return metalRepository.findLatestUniqueByName();
    }

    @GetMapping("/metal/history/{metalName}")
    public List<MetalPrice> getHistoryMetal(
            @PathVariable String metalName,
            @RequestParam Long from,
            @RequestParam Long to) {
        return metalRepository.findByNameAndTimestampBetweenOrderByTimestampAsc(metalName, from, to);
    }

    @GetMapping("/currency/lastprice/{currencyName}")
    public JSONObject getSberLatestCurrency(@PathVariable String currencyName) {
        return currencyRepository.findLatestByName(currencyName).get().toJsonObject();
    }

    @GetMapping("/currency/all")
    public List<CurrencyPrice> getSberAllCurrency() {
        return currencyRepository.findLatestUniqueByName();
    }

    @GetMapping("/currency/history/{currencyName}")
    public List<CurrencyPrice> getHistoryCurrency(
            @PathVariable String currencyName,
            @RequestParam Long from,
            @RequestParam Long to) {
        return currencyRepository.findByNameAndTimestampBetweenOrderByTimestampAsc(currencyName, from, to);
    }
}