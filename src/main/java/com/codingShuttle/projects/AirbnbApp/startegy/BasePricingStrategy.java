package com.codingShuttle.projects.AirbnbApp.startegy;

import com.codingShuttle.projects.AirbnbApp.entity.Inventory;

import java.math.BigDecimal;

public class BasePricingStrategy implements  PricingStrategy{

    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        return inventory.getRoom().getBasePrice();
        
    }
}
