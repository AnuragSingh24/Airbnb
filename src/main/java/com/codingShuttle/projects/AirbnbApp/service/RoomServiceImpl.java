package com.codingShuttle.projects.AirbnbApp.service;

import com.codingShuttle.projects.AirbnbApp.dto.RoomDto;
import com.codingShuttle.projects.AirbnbApp.entity.Hotel;
import com.codingShuttle.projects.AirbnbApp.entity.Room;
import com.codingShuttle.projects.AirbnbApp.exception.ResourceNotFoundException;
import com.codingShuttle.projects.AirbnbApp.repository.HotelRepository;
import com.codingShuttle.projects.AirbnbApp.repository.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomServiceImpl implements  RoomService {
    private final RoomRepository roomRepository;
    private  final ModelMapper modelMapper;
    private  final HotelRepository hotelRepository;
    private  final InventoryService inventoryService;

    @Override
    public RoomDto createNewRoom(Long hotelId , RoomDto roomDto) {
       log.info("Creating a new room in hotel with Id: {}", hotelId);
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found"));
        Room room = modelMapper.map(roomDto , Room.class);
        room.setHotel(hotel);
        room = roomRepository.save(room);


        if(hotel.getActive()){  //if the room is active then create the inventory
           inventoryService.intializeRoomForAYear(room);
        }

        return modelMapper.map(room , RoomDto.class);
    }

    @Override
    public List<RoomDto> getAllRoomsInHotel(Long hotelId) {
        log.info("Getting all rooms  in hotel with Id: {}", hotelId);
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found"));


       return hotel.getRooms()
               .stream()
               .map((element) -> modelMapper.map(element, RoomDto.class))
               .collect(Collectors.toList()); //stream to a list
    }

    @Override
    public RoomDto getRoomById(Long roomId) {
        log.info("Getting the room ith Id: {}", roomId);
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
        return modelMapper.map(room, RoomDto.class);
    }

    @Override
    @Transactional
    public void deleteRoomById(Long roomId) {
   log.info("Deleting the room with ID: {}", roomId);
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));

     inventoryService.deleteAllInventories(room);
        roomRepository.deleteById(roomId);

        //its getting error

    }
}
