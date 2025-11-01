package prices.model;

import net.minidev.json.JSONObject;
import prices.utils.JsonUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

public class Price {

    private Long id;
    private String name;
    private BigDecimal buyPrice;
    private BigDecimal sellPrice;
    private Long timestamp = Instant.now().toEpochMilli();
    private String bank;

    public Price(String name, BigDecimal buyPrice, BigDecimal sellPrice, String bank) {
        this.name = Objects.requireNonNull(name);
        this.buyPrice = Objects.requireNonNull(buyPrice);
        this.sellPrice = Objects.requireNonNull(sellPrice);
        this.bank = bank;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
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
        return JsonUtils.getPriceToJson(name, buyPrice, sellPrice, timestamp, bank);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Price that)) return false;
        return Objects.equals(name, that.name) &&
                Objects.equals(buyPrice, that.buyPrice) &&
                Objects.equals(sellPrice, that.sellPrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, buyPrice, sellPrice);
    }
}
