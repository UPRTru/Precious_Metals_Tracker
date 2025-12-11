package com.precious.user.service;

import com.precious.shared.dto.CheckPrice;
import com.precious.user.model.ScheduledPrice;
import com.precious.user.model.User;
import com.precious.user.repository.ScheduledPriceRepository;
import com.precious.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ScheduledPriceRepository scheduledPriceRepository;

    public UserService(UserRepository userRepository,
                       ScheduledPriceRepository scheduledPriceRepository) {
        this.userRepository = userRepository;
        this.scheduledPriceRepository = scheduledPriceRepository;
    }

    public void register(String email, String rawPassword, String timezone) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Пользователь с таким email уже существует");
        }
        User user = new User(email, rawPassword, timezone);
        userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void addScheduledPrice(String email, CheckPrice checkPrice) {
        User user = findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        ScheduledPrice scheduledPrice = new ScheduledPrice(user, checkPrice.bank().name(), checkPrice.typePrice().name(), checkPrice.currentPrice().name(), checkPrice.name(), checkPrice.price());
        scheduledPriceRepository.save(scheduledPrice);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}