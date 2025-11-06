package prices.model;

import com.precious.shared.model.JsonKeys;
import jakarta.persistence.*;
import net.minidev.json.JSONObject;
import prices.utils.JsonUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Objects;

@Entity
@Table(name = "metal_prices", indexes = {
        @Index(name = "idx_name", columnList = "name"),
        @Index(name = "idx_timestamp", columnList = "timestamp")
})
public class MetalPrice implements PriceInterface {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal buyPrice;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal sellPrice;

    @Column(nullable = false)
    private final Long timestamp = Instant.now().atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();

    @Column(nullable = false)
    private String bank;

    @Column(nullable = false)
    private String weight;

    protected MetalPrice() {
    }

    public MetalPrice(String name, BigDecimal buyPrice, BigDecimal sellPrice, String bank) {
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

    public String getWeight() {
        return weight;
    }

    @Override
    public JSONObject toJsonObject() {
        JSONObject result = JsonUtils.getPriceToJson(name, buyPrice, sellPrice, timestamp, bank);
        result = JsonUtils.addCustomField(result, JsonKeys.CustomFields.WEIGHT.getKey(), weight);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MetalPrice)) return false;
        MetalPrice that = (MetalPrice) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(buyPrice, that.buyPrice) &&
                Objects.equals(sellPrice, that.sellPrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, buyPrice, sellPrice);
    }
}