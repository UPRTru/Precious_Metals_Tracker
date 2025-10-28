package prices.model;

import com.precious.shared.model.Banks;
import jakarta.persistence.*;
import net.minidev.json.JSONObject;
import prices.utils.JsonUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "currency_prices", indexes = {
        @Index(name = "idx_currency_name", columnList = "currency_name"),
        @Index(name = "idx_timestamp", columnList = "timestamp")
})
public class CurrencyPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String currencyName;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal buyPrice;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal sellPrice;

    @Column(nullable = false)
    private Long timestamp = Instant.now().toEpochMilli();

    @Column(nullable = false)
    private String bank;

    protected CurrencyPrice() {
    }

    public CurrencyPrice(String currencyName, BigDecimal buyPrice, BigDecimal sellPrice, String bank) {
        this.currencyName = Objects.requireNonNull(currencyName);
        this.buyPrice = Objects.requireNonNull(buyPrice);
        this.sellPrice = Objects.requireNonNull(sellPrice);
        this.bank = bank;
    }

    public Long getId() {
        return id;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public BigDecimal getBuyPrice() {
        return buyPrice;
    }

    public BigDecimal getSellPrice() {
        return sellPrice;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public String getBank() {
        return bank;
    }

    public JSONObject toJsonObject() {
        return JsonUtils.getPriceToJson(currencyName, buyPrice, sellPrice, timestamp, bank);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CurrencyPrice that)) return false;
        return Objects.equals(currencyName, that.currencyName) &&
                Objects.equals(buyPrice, that.buyPrice) &&
                Objects.equals(sellPrice, that.sellPrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currencyName, buyPrice, sellPrice);
    }
}