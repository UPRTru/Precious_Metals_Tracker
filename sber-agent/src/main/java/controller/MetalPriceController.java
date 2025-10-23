package controller;

import model.MetalPrice;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import repository.MetalPriceRepository;
import service.MetalPriceService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/sber")
public class MetalPriceController {

    private final MetalPriceService priceService;
    private final MetalPriceRepository repository;

    public MetalPriceController(MetalPriceService priceService, MetalPriceRepository repository) {
        this.priceService = priceService;
        this.repository = repository;
    }

    @GetMapping("/latest/{metalName}")
    public MetalPrice getLatest(@PathVariable String metalName) {
        return priceService.getLatestPrice(metalName);
    }

    @GetMapping("/history/{metalName}")
    public List<MetalPrice> getHistory(
            @PathVariable String metalName,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return repository.findByMetalNameAndTimestampBetweenOrderByTimestampAsc(metalName, from, to);
    }
}