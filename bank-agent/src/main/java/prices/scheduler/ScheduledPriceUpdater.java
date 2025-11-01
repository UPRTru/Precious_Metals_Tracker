package prices.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import prices.service.PriceService;
import prices.service.TypePrice;

import java.util.Random;

@Component
public class ScheduledPriceUpdater {

    Random random = new Random();

    private static final Logger log = LoggerFactory.getLogger(ScheduledPriceUpdater.class);

    private final PriceService priceService;

    public ScheduledPriceUpdater(PriceService priceService) {
        this.priceService = priceService;
    }

//    @Scheduled(fixedRate = 300_000) // 5 минут
    @Scheduled(fixedRate = 900_000) // 15 минут
    public void updatePrices() {
        log.info("Fetching and updating metal prices...");
        try {
            int randomNumber = random.nextInt(120001);
            Thread.sleep(randomNumber);
            priceService.updatePrices(TypePrice.SBER_METAL);
            randomNumber = random.nextInt(140001) + 80000;
            Thread.sleep(randomNumber);
            priceService.updatePrices(TypePrice.SBER_CURRENCY);
            log.info("Metal prices updated successfully.");
        } catch (Exception e) {
            log.error("Error during price update", e);
        }
    }
}