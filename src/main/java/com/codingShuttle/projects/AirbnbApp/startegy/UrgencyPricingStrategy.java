package com.codingShuttle.projects.AirbnbApp.startegy;


import com.codingShuttle.projects.AirbnbApp.entity.Inventory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@RequiredArgsConstructor
public class UrgencyPricingStrategy implements PricingStrategy {
    private final PricingStrategy wrapped;

    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        BigDecimal price = wrapped.calculatePrice(inventory);
        LocalDate currentDate = LocalDate.now();
        if(!inventory.getDate().isBefore(currentDate) && inventory.getDate().isBefore(currentDate.plusDays(7)))
        {
            price = price.multiply(BigDecimal.valueOf(1.5));
        }
       return price;
    }
}
