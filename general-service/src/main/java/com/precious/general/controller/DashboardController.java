package com.precious.general.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class DashboardController {

    @Value("${bank.agent.url}")
    private String bankAgentUrl;

    @Value("${user.service.url}")
    private String userServiceUrl;

    @GetMapping("/")
    public String dashboard() {
        return "index";
    }

    @GetMapping("/api/modules/status")
    @ResponseBody
    public Map<String, Boolean> moduleStatus() {
        Map<String, Boolean> status = new HashMap<>();
        status.put("bankAgent", isReachable(bankAgentUrl + "/health"));
        status.put("userService", isReachable(userServiceUrl + "/health"));
        return status;
    }

    private boolean isReachable(String url) {
        try {
            java.net.URL u = new java.net.URL(url);
            java.net.HttpURLConnection h = (java.net.HttpURLConnection) u.openConnection();
            h.setRequestMethod("GET");
            h.setConnectTimeout(1000);
            h.setReadTimeout(1000);
            return h.getResponseCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }
}
