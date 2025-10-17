package com.precious.metal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MetalPriceServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(MetalPriceServiceApplication.class, args);
    }
}