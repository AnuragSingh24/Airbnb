package com.codingShuttle.projects.AirbnbApp.controller;

import com.codingShuttle.projects.AirbnbApp.dto.HotelDto;
import com.codingShuttle.projects.AirbnbApp.entity.Hotel;
import com.codingShuttle.projects.AirbnbApp.service.HotelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/hotels")
@RequiredArgsConstructor
@Slf4j
public class HotelController {

  private final HotelService hotelService; //we dont directely import the service class by the help of interface we are calling

  @PostMapping
    public ResponseEntity<HotelDto> createNewHotel(@RequestBody HotelDto hotelDto)
  {
      log.info("Attempting to create a new hotel with name" + hotelDto.getName());

      HotelDto hotel = hotelService.createNewHotel(hotelDto);

      return new ResponseEntity<>(hotel , HttpStatus.CREATED);

  }

  @GetMapping("/{hotel_id}")
    public ResponseEntity<HotelDto> getHotelById(@PathVariable Long hotel_id)
  {
      HotelDto hotelDto = hotelService.getHotelById(hotel_id);
      return ResponseEntity.ok(hotelDto);
  }

  @PutMapping("/{hotel_id}")
    public ResponseEntity<HotelDto> updateHotelById(@PathVariable Long hotel_id , @RequestBody HotelDto hotelDto)
  {
      HotelDto hotel = hotelService.updateHotelById(hotel_id , hotelDto);
      return ResponseEntity.ok(hotel);
  }

  @DeleteMapping("/{hotel_id}")
  public ResponseEntity<Void> DeleteHotelById(@PathVariable Long hotel_id)
  {
      hotelService.deleteHotelById(hotel_id);
       return ResponseEntity.noContent().build();
  }

  @PutMapping("/{hotel_id}/activate")
  public ResponseEntity<Void> activateHotel(@PathVariable Long hotel_id)
  {
      hotelService.activateHotel(hotel_id);
      return ResponseEntity.noContent().build();
  }





}
