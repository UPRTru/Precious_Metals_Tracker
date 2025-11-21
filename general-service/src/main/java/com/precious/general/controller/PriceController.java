package com.precious.general.controller;

import com.precious.general.service.ServiceCheckPrice;
import net.minidev.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class PriceController {

    private final ServiceCheckPrice serviceCheckPrice;

    public PriceController(ServiceCheckPrice serviceCheckPrice) {
        this.serviceCheckPrice = serviceCheckPrice;
    }

    @PostMapping("/check/{email}")
    public void check(@RequestBody JSONObject jsonObject, @PathVariable String email) {
        serviceCheckPrice.checkPrice(email, jsonObject);
    }
}