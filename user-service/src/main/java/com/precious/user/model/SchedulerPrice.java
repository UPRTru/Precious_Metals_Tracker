package com.precious.user.model;

import jakarta.persistence.*;

@Entity
@Table(name = "scheduler_price")
public class SchedulerPrice {

    private static final String EMPTY_JSON = "{}";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(columnDefinition = "json")
    private String json = EMPTY_JSON;
}
