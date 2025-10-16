package com.pprecious.gateway.controller;

import com.precious.gateway.service.ServiceRegistry;
import com.precious.shared.model.Metal;
import com.precious.shared.model.Currency;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class GatewayController {

    private final ServiceRegistry serviceRegistry;

    public GatewayController(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    @GetMapping("/")
    public String index(Model model) {
        boolean userRegistered = serviceRegistry.isUserServiceRegistered();
        model.addAttribute("userRegistered", userRegistered);

        if (userRegistered) {
            Map<String, Object> services = serviceRegistry.getActiveServices().entrySet().stream()
                    .filter(e -> !e.getKey().equals("gateway-service"))
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            e -> e.getValue().getFrontendUrl()
                    ));
            model.addAttribute("services", services);
        }

        // Справочники
        List<String> metals = Arrays.stream(Metal.values())
                .map(Metal::getDisplayName)
                .collect(Collectors.toList());
        List<String> currencies = Arrays.stream(Currency.values())
                .map(Enum::name)
                .collect(Collectors.toList());

        model.addAttribute("metals", metals);
        model.addAttribute("currencies", currencies);

        return "index";
    }
}