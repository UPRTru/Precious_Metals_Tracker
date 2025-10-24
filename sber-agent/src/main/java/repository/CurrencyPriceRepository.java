package repository;

import model.CurrencyPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CurrencyPriceRepository extends JpaRepository<CurrencyPrice, Long> {

    @Query("SELECT p FROM CurrencyPrice p WHERE p.currencyName = :name ORDER BY p.timestamp DESC LIMIT 1")
    Optional<CurrencyPrice> findLatestByCurrencyName(String name);

    List<CurrencyPrice> findByCurrencyNameAndTimestampBetweenOrderByTimestampAsc(
            String currencyName, LocalDateTime from, LocalDateTime to);
}