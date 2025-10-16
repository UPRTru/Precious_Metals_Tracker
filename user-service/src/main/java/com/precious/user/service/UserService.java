package com.precious.user.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.precious.user.model.User;
import com.precious.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.objectMapper = objectMapper;
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public User register(String email, String rawPassword) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Пользователь с таким email уже существует");
        }
        User user = new User(email, passwordEncoder.encode(rawPassword));
        // Инициализируем пустые JSON-объекты
        user.setMetalBuyHistoryFromJson(objectMapper.createObjectNode());
        user.setMetalSellHistoryFromJson(objectMapper.createObjectNode());
        user.setCurrencyBuyHistoryFromJson(objectMapper.createObjectNode());
        user.setCurrencySellHistoryFromJson(objectMapper.createObjectNode());
        user.setPreferencesFromJson(objectMapper.createObjectNode());
        return userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void addMetalBuyRecord(String email, String metalName, double price) {
        User user = findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        ObjectNode history = (ObjectNode) user.getMetalBuyHistoryAsJson();
        if (!history.has(metalName)) {
            history.set(metalName, objectMapper.createArrayNode());
        }
        ArrayNode metalArray = (ArrayNode) history.get(metalName);
        ObjectNode record = objectMapper.createObjectNode();
        record.put("date", LocalDate.now().toString());
        record.put("price", price);
        metalArray.add(record);

        user.setMetalBuyHistoryFromJson(history);
        userRepository.save(user);
    }
}