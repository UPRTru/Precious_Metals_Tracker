package prices.model;

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

    protected CurrencyPrice() {
    }

    public CurrencyPrice(String currencyName, BigDecimal buyPrice, BigDecimal sellPrice) {
        this.currencyName = Objects.requireNonNull(currencyName);
        this.buyPrice = Objects.requireNonNull(buyPrice);
        this.sellPrice = Objects.requireNonNull(sellPrice);
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

    public JSONObject toJsonObject() {
        return JsonUtils.getPriceToJson(currencyName, buyPrice, sellPrice, timestamp);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CurrencyPrice)) return false;
        CurrencyPrice that = (CurrencyPrice) o;
        return Objects.equals(currencyName, that.currencyName) &&
                Objects.equals(buyPrice, that.buyPrice) &&
                Objects.equals(sellPrice, that.sellPrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currencyName, buyPrice, sellPrice);
    }
}