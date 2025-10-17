package com.precious.metal.repository;

import com.precious.metal.model.MetalPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MetalPriceRepository extends JpaRepository<MetalPrice, Long> {

    @Query("SELECT p FROM MetalPrice p WHERE p.metalName = :name ORDER BY p.timestamp DESC LIMIT 1")
    Optional<MetalPrice> findLatestByMetalName(String name);

    List<MetalPrice> findByMetalNameAndTimestampBetweenOrderByTimestampAsc(
            String metalName, LocalDateTime from, LocalDateTime to);
}