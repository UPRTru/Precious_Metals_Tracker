package prices.controller;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.web.bind.annotation.*;
import prices.service.PriceService;
import prices.service.TypePrice;

@RestController
@RequestMapping("/api/sber")
public class MetalPriceController {

    private final PriceService priceService;
//    private final MetalPriceRepository repository;

    public MetalPriceController(PriceService priceService) {
//    public MetalPriceController(PriceService priceService, MetalPriceRepository repository) {
        this.priceService = priceService;
//        this.repository = repository;
    }

    @GetMapping("/sber/latest/metal/{metalName}")
    public JSONObject getSberLatestMetal(@PathVariable String metalName) {
        return priceService.getPrices(TypePrice.SBER_METAL, metalName);
    }

    @GetMapping("/all/metal")
    public JSONArray getSberAllMetal() {
        return priceService.getPrices(TypePrice.SBER_METAL);
    }

//    @GetMapping("/history/{metalName}")
//    public List<MetalPrice> getHistory(
//            @PathVariable String metalName,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
//        return repository.findByMetalNameAndTimestampBetweenOrderByTimestampAsc(metalName, from, to);
//    }
}