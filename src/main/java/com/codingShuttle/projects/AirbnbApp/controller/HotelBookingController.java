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
import java.util.Map;

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

    @PostMapping("/{bookingId}/payments")
    public ResponseEntity<Map<String , String>> initiatePayment(@PathVariable Long bookingId)
    {

        String sessionUrl = bookingService.intiatePayment(bookingId);

      return ResponseEntity.ok(Map.of("sessionUrl",sessionUrl));
    }

    @PostMapping("/{bookingId}/cancel")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long bookingId)
    {

         bookingService.cancelBooking(bookingId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{bookingId}/status")
    public ResponseEntity<Map<String , String>> getBookingStatus(@PathVariable Long bookingId)
    {

        return ResponseEntity.ok(Map.of("status", bookingService.getBookingStatus(bookingId)));
    }


}
