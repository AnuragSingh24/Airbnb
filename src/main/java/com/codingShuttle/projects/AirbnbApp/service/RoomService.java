package com.codingShuttle.projects.AirbnbApp.service;


import com.codingShuttle.projects.AirbnbApp.dto.RoomDto;

import java.util.List;

public interface RoomService {

    RoomDto createNewRoom(Long hotelId , RoomDto roomDto);

    List<RoomDto> getAllRoomsInHotel(Long hotel_id);

    RoomDto getRoomById(Long roomId);

    void deleteRoomById(Long roomId);

}
