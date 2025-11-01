package prices.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import prices.model.CurrencyPrice;

import java.util.List;
import java.util.Optional;

@Repository
public interface CurrencyPriceRepository extends JpaRepository<CurrencyPrice, Long> {
}