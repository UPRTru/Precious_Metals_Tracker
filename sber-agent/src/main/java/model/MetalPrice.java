package model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "metal_prices", indexes = {
        @Index(name = "idx_metal_name", columnList = "metal_name"),
        @Index(name = "idx_timestamp", columnList = "timestamp")
})
public class MetalPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String metalName; // "Золото"

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal buyPrice;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal sellPrice;

    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    // --- constructors ---
    protected MetalPrice() {}

    public MetalPrice(String metalName, BigDecimal buyPrice, BigDecimal sellPrice) {
        this.metalName = Objects.requireNonNull(metalName);
        this.buyPrice = Objects.requireNonNull(buyPrice);
        this.sellPrice = Objects.requireNonNull(sellPrice);
    }

    // --- getters ---
    public Long getId() { return id; }
    public String getMetalName() { return metalName; }
    public BigDecimal getBuyPrice() { return buyPrice; }
    public BigDecimal getSellPrice() { return sellPrice; }
    public LocalDateTime getTimestamp() { return timestamp; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MetalPrice)) return false;
        MetalPrice that = (MetalPrice) o;
        return Objects.equals(metalName, that.metalName) &&
                Objects.equals(buyPrice, that.buyPrice) &&
                Objects.equals(sellPrice, that.sellPrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(metalName, buyPrice, sellPrice);
    }
}