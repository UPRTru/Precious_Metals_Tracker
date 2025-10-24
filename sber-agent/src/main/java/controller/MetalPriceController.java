package controller;

import model.MetalPrice;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import repository.MetalPriceRepository;
import service.PriceService;
import service.TypePrice;

import java.time.LocalDateTime;
import java.util.List;

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

    @GetMapping("/sber/all/metal/{metalName}")
    public JSONArray getSberAllMetal(@PathVariable String metalName) {
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