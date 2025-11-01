package prices.controller;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.web.bind.annotation.*;
import prices.model.MetalPrice;
import prices.repository.MetalPriceRepository;
import prices.service.PriceService;
import prices.service.TypePrice;

import java.util.List;

@RestController
@RequestMapping("/sber/metal")
public class MetalPriceController {

    private final MetalPriceRepository repository;
    private final PriceService priceService;

    public MetalPriceController(PriceService priceService, MetalPriceRepository repository) {
        this.repository = repository;
        this.priceService = priceService;
    }

    @GetMapping("/lastprice/{metalName}")
    public JSONObject getSberLatestMetal(@PathVariable String metalName) {
        return priceService.getPrices(TypePrice.SBER_METAL, metalName);
    }

    @GetMapping("/all")
    public JSONArray getSberAllMetal() {
        return priceService.getPrices(TypePrice.SBER_METAL);
    }

    @GetMapping("/history/{metalName}")
    public List<MetalPrice> getHistory(
            @PathVariable String metalName,
            @RequestParam Long from,
            @RequestParam Long to) {
        return repository.findByMetalNameAndTimestampBetweenOrderByTimestampAsc(metalName, from, to);
    }
}