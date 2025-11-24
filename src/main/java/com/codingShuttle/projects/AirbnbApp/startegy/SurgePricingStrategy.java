package com.codingShuttle.projects.AirbnbApp.startegy;

import com.codingShuttle.projects.AirbnbApp.entity.Inventory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class SurgePricingStrategy implements PricingStrategy{

    private final PricingStrategy wrapped;

    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        BigDecimal price  = wrapped.calculatePrice(inventory);
        return  price.multiply(inventory.getSurgeFactor());
    }


}
