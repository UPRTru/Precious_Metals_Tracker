package prices.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import prices.service.PriceService;
import prices.service.TypePrice;

@Component
public class ScheduledPriceUpdater {

    private static final Logger log = LoggerFactory.getLogger(ScheduledPriceUpdater.class);

    private final PriceService priceService;

    public ScheduledPriceUpdater(PriceService priceService) {
        this.priceService = priceService;
    }

    @Scheduled(fixedRate = 300_000) // 5 минут
    public void updatePrices() {
        log.info("Fetching and updating metal prices...");
        try {
            priceService.updatePrices(TypePrice.SBER_METAL);
            priceService.updatePrices(TypePrice.SBER_CURRENCY);
            log.info("Metal prices updated successfully.");
        } catch (Exception e) {
            log.error("Error during price update", e);
        }
    }
}