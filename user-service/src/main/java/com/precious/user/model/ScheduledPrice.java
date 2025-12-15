package com.precious.user.model;

import com.precious.shared.dto.CheckPrice;
import com.precious.shared.enums.Banks;
import com.precious.shared.enums.CurrentPrice;
import com.precious.shared.enums.TypePrice;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "scheduler_price")
public class ScheduledPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String bank;

    @Column(nullable = false)
    private String typePrice;

    @Column(nullable = false)
    private String currentPrice;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal price;

    public ScheduledPrice() {}

    public ScheduledPrice(User user, String bank, String typePrice, String currentPrice, String name, BigDecimal price) {
        this.user = user;
        this.bank = bank;
        this.typePrice = typePrice;
        this.currentPrice = currentPrice;
        this.name = name;
        this.price = price;
    }

    public CheckPrice getCheckPrice() {
        return new CheckPrice(Banks.valueOf(bank), TypePrice.valueOf(typePrice), CurrentPrice.valueOf(currentPrice), name, price);
    }

    @Override
    public String toString() {
        return "ScheduledPrice{" +
                ", bank='" + bank + '\'' +
                ", typePrice='" + typePrice + '\'' +
                ", currentPrice='" + currentPrice + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                '}';
    }
}
