package com.precious.user.model;

import jakarta.persistence.*;

@Entity
@Table(name = "scheduler_price")
public class ScheduledPrice {

    private static final String EMPTY_JSON = "{}";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(columnDefinition = "json")
    private String json = EMPTY_JSON;

    public ScheduledPrice() {
    }

    public ScheduledPrice(User user, String json) {
        this.user = user;
        this.json = json;
    }

    public String getJson() {
        return json;
    }
}
