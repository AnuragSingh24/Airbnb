package com.codingShuttle.projects.AirbnbApp.service;

import com.codingShuttle.projects.AirbnbApp.dto.HotelDto;
import com.codingShuttle.projects.AirbnbApp.dto.HotelPriceDto;
import com.codingShuttle.projects.AirbnbApp.dto.HotelSearchRequest;
import com.codingShuttle.projects.AirbnbApp.entity.Room;
import org.springframework.data.domain.Page;

public interface InventoryService {

    void intializeRoomForAYear(Room room);

    void deleteAllInventories(Room room);

    Page<HotelPriceDto> searchHotels(HotelSearchRequest hotelSearchRequest);

}
