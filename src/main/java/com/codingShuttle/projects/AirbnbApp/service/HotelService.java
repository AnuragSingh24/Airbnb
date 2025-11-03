package com.codingShuttle.projects.AirbnbApp.service;

import com.codingShuttle.projects.AirbnbApp.dto.HotelDto;
import com.codingShuttle.projects.AirbnbApp.entity.Hotel;

public interface HotelService {

    HotelDto createNewHotel(HotelDto hotelDto);

     HotelDto getHotelById(Long id);

     HotelDto updateHotelById(Long id , HotelDto hotelDto);

     void deleteHotelById(Long id);

     void activateHotel(Long hotelId);

}
