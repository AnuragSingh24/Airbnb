package com.codingShuttle.projects.AirbnbApp.controller;


import com.codingShuttle.projects.AirbnbApp.dto.HotelDto;
import com.codingShuttle.projects.AirbnbApp.dto.HotelInfoDto;
import com.codingShuttle.projects.AirbnbApp.dto.HotelPriceDto;
import com.codingShuttle.projects.AirbnbApp.dto.HotelSearchRequest;
import com.codingShuttle.projects.AirbnbApp.repository.InventoryRepository;
import com.codingShuttle.projects.AirbnbApp.service.HotelService;
import com.codingShuttle.projects.AirbnbApp.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/hotels")
@RequiredArgsConstructor
public class HotelBrowseController {
    private final InventoryService  inventoryService;

    private final HotelService  hotelService;
    @GetMapping("/search")
    public ResponseEntity<Page<HotelPriceDto>> searchHotels(@RequestBody HotelSearchRequest hotelSearchRequest)
    {
        Page<HotelPriceDto> hotelPage = inventoryService.searchHotels(hotelSearchRequest);

        return ResponseEntity.ok(hotelPage);
    }

    @GetMapping("/{hotel_id}/info")
    public ResponseEntity<HotelInfoDto> getHotelById(@PathVariable Long hotel_id)
    {
        HotelInfoDto hotelinfo =  hotelService.getHotelInfoById(hotel_id);
        return  ResponseEntity.ok(hotelinfo);
    }


}
