package com.codingShuttle.projects.AirbnbApp.startegy;

import com.codingShuttle.projects.AirbnbApp.entity.Inventory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

public interface PricingStrategy {


    BigDecimal calculatePrice(Inventory inventory);

}
