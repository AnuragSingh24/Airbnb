package com.codingShuttle.projects.AirbnbApp.dto;

import com.codingShuttle.projects.AirbnbApp.entity.Hotel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data

@AllArgsConstructor
@NoArgsConstructor
public class HotelPriceDto {

    private Hotel hotel;

    private Double price;
}
