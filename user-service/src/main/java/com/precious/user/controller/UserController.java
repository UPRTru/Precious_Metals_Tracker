package com.precious.user.controller;

import com.precious.shared.enums.JsonKeys;
import com.precious.user.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import net.minidev.json.JSONObject;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new RegistrationForm());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid RegistrationForm form, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "register";
        }
        try {
            userService.register(form.email, form.password, form.timezone);
            model.addAttribute("message", "Регистрация успешна! Войдите в систему.");
            return "login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/profile")
    public String profile() {
        return "profile";
    }

    @PostMapping("/add/scheduled_price")
    public void addScheduledPrice(@Valid String email, @Valid Price Price) {
        userService.addScheduledPrice(email, Price.toJson());
    }

    // Widget для встраивания в gateway
    @GetMapping("/widget")
    public String widget() {
        return "widget";
    }

    private record Price(@NonNull String bank, @NonNull String typePrice, @NonNull String name, @NonNull String currentPrice, @NonNull String price) {
        public JSONObject toJson() {
            return new JSONObject()
                    .appendField(JsonKeys.BANK.getKey(), bank)
                    .appendField(JsonKeys.TYPE_PRICE.getKey(), typePrice)
                    .appendField(JsonKeys.NAME.getKey(), name)
                    .appendField(JsonKeys.CURRENT_PRICE.getKey(), currentPrice)
                    .appendField(JsonKeys.CustomFields.PRICE.getKey(), price);
        }
    }

    public record RegistrationForm(@NotBlank @Email String email, @NotBlank String password, @NotBlank String timezone) {}
}