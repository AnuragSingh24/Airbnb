package com.codingShuttle.projects.AirbnbApp.service;

import com.codingShuttle.projects.AirbnbApp.dto.HotelDto;
import com.codingShuttle.projects.AirbnbApp.dto.HotelPriceDto;
import com.codingShuttle.projects.AirbnbApp.dto.HotelSearchRequest;
import com.codingShuttle.projects.AirbnbApp.entity.Hotel;
import com.codingShuttle.projects.AirbnbApp.entity.Inventory;
import com.codingShuttle.projects.AirbnbApp.entity.Room;
import com.codingShuttle.projects.AirbnbApp.repository.HotelMinPriceRepository;
import com.codingShuttle.projects.AirbnbApp.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements  InventoryService {
      private  final InventoryRepository inventoryRepository;
      private  final ModelMapper modelMapper;
      private final HotelMinPriceRepository hotelMinPriceRepository;


    @Override
    public void intializeRoomForAYear(Room room) {   //assigning the room for year in the inventory
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusYears(1);
            for(; !today.isAfter(endDate); today = today.plusDays(1))
        {
            Inventory inventory = Inventory.builder()
                    .hotel(room.getHotel())
                    .room(room)
                    .bookedCount(0)
                    .reserveCount(0)
                    .city(room.getHotel().getCity())
                    .date(today)
                    .price(room.getBasePrice())
                    .surgeFactor(BigDecimal.ONE)
                    .totalCount(room.getTotalCount())
                    .closed(false)
                    .build();
            inventoryRepository.save(inventory);
        }
    }

    @Override
    public void deleteAllInventories(Room room)  {
        log.info("Deleting the inventories of room with id : {}", room.getId());
        inventoryRepository.deleteByRoom(room);
    }

    @Override
    public Page<HotelPriceDto> searchHotels(HotelSearchRequest hotelSearchRequest) {
        log.info("Searching hotels with request: {}", hotelSearchRequest);
        
        Pageable pageable = PageRequest.of(hotelSearchRequest.getPage(), hotelSearchRequest.getSize());
        Long dateCount = ChronoUnit.DAYS.between(hotelSearchRequest.getStartDate(), hotelSearchRequest.getEndDate()) + 1;
        
        log.info("Date count calculated: {}", dateCount);


        //business logic - 90  days


        Page<HotelPriceDto> hotelPage = hotelMinPriceRepository.findHotelsWithAvailableInventory(
                hotelSearchRequest.getCity(),
                hotelSearchRequest.getStartDate(),
                hotelSearchRequest.getEndDate(),
                pageable
        );
        
        log.info("Found {} hotels", hotelPage.getTotalElements());
        
        return hotelPage;
    }
}
