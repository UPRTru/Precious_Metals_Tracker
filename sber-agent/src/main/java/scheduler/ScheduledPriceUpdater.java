package scheduler;

import com.precious.metal.service.MetalPriceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledPriceUpdater {

    private static final Logger log = LoggerFactory.getLogger(ScheduledPriceUpdater.class);

    private final MetalPriceService priceService;

    public ScheduledPriceUpdater(MetalPriceService priceService) {
        this.priceService = priceService;
    }

    @Scheduled(fixedRate = 300_000) // 5 минут
    public void updatePrices() {
        log.info("Fetching and updating metal prices...");
        try {
            priceService.fetchAndSaveIfChanged();
            log.info("Metal prices updated successfully.");
        } catch (Exception e) {
            log.error("Error during price update", e);
        }
    }
}