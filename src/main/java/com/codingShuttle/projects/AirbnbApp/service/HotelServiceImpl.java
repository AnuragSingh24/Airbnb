package com.codingShuttle.projects.AirbnbApp.service;

import com.codingShuttle.projects.AirbnbApp.dto.HotelDto;
import com.codingShuttle.projects.AirbnbApp.entity.Hotel;
import com.codingShuttle.projects.AirbnbApp.entity.Room;
import com.codingShuttle.projects.AirbnbApp.exception.ResourceNotFoundException;
import com.codingShuttle.projects.AirbnbApp.repository.HotelRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor //this is used for constructor injection
public class HotelServiceImpl implements HotelService{

    private final HotelRepository hotelRepository;
    private final ModelMapper modelMapper;
    private  final InventoryService inventoryService;


    @Override
    public HotelDto createNewHotel(HotelDto hotelDto) {
        log.info("Creatingt a new hotel with name: {}", hotelDto.getName());
        Hotel hotel = modelMapper.map(hotelDto , Hotel.class);
        hotel.setActive(false); //set the hotel false we can create a api for active it
        hotel = hotelRepository.save(hotel);
        log.info("Created a new hotel with ID: {}", hotelDto.getId());
        return modelMapper.map(hotel , HotelDto.class);

    }

    @Override
    public HotelDto getHotelById(Long id) {
        log.info("Getting the hotel with id {}", id);
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found"));
        return modelMapper.map(hotel , HotelDto.class);
    }

    @Override
    public HotelDto updateHotelById(Long id , HotelDto hotelDto) {
        log.info("updating the hotel with id {}", id);
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found"));

        modelMapper.map(hotelDto , hotel);
        hotel.setId(id);
        hotel = hotelRepository.save(hotel);
        return modelMapper.map(hotel , HotelDto.class);

    }

    @Override
    @Transactional
    public void deleteHotelById(Long id) {
              Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found"));
              hotelRepository.deleteById(id);
        for(Room room : hotel.getRooms())
        {
            inventoryService.deletFutureInventories(room);
        }
    }

    @Override
    @Transactional
    public void activateHotel(Long hotelId) {
        log.info("Activating the hotel with id {}", hotelId);
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found"));

       hotel.setActive(true);
        //assuming only do it once

         for(Room room : hotel.getRooms())
         {
             inventoryService.intializeRoomForAYear(room);
         }

    }
}
