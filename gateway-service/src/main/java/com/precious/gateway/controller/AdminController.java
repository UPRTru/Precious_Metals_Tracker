package com.precious.gateway.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.precious.gateway.client.UserServiceClient;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

@Controller
public class AdminController {

    @Value("${user.service.url:http://localhost:8081}")
    private String userServiceUrl;
    private final UserServiceClient userServiceClient;

    public AdminController(UserServiceClient userServiceClient) {
        this.userServiceClient = userServiceClient;
    }

    @GetMapping("/admin/users")
    public String listUsers(Model model) {
        Flux<UserServiceClient.UserDto> users = userServiceClient.getAllUsers();
        try {
            List<UserServiceClient.UserDto> userList = users.collectList().block(Duration.ofSeconds(5));
            model.addAttribute("users", userList);
        } catch (Exception e) {
            model.addAttribute("error", "Не удалось загрузить пользователей");
        }
        return "admin/users";
    }

    @PostMapping("/admin/trigger-check")
    public String triggerCheck(@RequestParam String email) {
        userServiceClient.triggerPriceCheckForUser(email).block();
        return "redirect:/admin/users?triggered=" + email;
    }

    @PostMapping("/admin/users/{email}/preferences")
    public String updatePreferences(
            @PathVariable String email,
            @RequestParam String preferencesJson,
            Model model) { // ← добавлен параметр Model

        try {
            JsonNode prefs = new ObjectMapper().readTree(preferencesJson);
            userServiceClient.updatePreferences(email, prefs).block();
            return "redirect:/admin/users?updated=" + email;
        } catch (Exception e) {
            model.addAttribute("error", "Некорректный JSON: " + e.getMessage());
            return listUsers(model); // теперь model доступен
        }
    }

    @GetMapping("/admin/notifications/export")
    public void exportNotifications(HttpServletResponse response) throws IOException {
        response.sendRedirect(userServiceUrl + "/api/admin/notifications/export");
    }
}