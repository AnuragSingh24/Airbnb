package com.codingShuttle.projects.AirbnbApp.controller;


import com.codingShuttle.projects.AirbnbApp.dto.BookingDto;
import com.codingShuttle.projects.AirbnbApp.dto.BookingRequest;
import com.codingShuttle.projects.AirbnbApp.dto.GuestDto;
import com.codingShuttle.projects.AirbnbApp.service.BookingService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/bookings")
public class HotelBookingController     {

    private  final BookingService bookingService;


   @PostMapping("/init")
    public ResponseEntity<BookingDto> initialiseBooking(@RequestBody BookingRequest bookingRequest)
    {

       return ResponseEntity.ok(bookingService.initialiseBooking(bookingRequest));
    }


    @PostMapping("/{bookingId}/addGuests")
    public ResponseEntity<BookingDto> addGuests( @PathVariable Long bookingId, @RequestBody List<GuestDto> guestDto)
    {
        return ResponseEntity.ok(bookingService.addGuests( bookingId, guestDto));
    }

}
