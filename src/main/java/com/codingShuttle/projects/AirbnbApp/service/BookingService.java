package com.codingShuttle.projects.AirbnbApp.service;

import com.codingShuttle.projects.AirbnbApp.dto.BookingDto;
import com.codingShuttle.projects.AirbnbApp.dto.BookingRequest;
import com.codingShuttle.projects.AirbnbApp.dto.GuestDto;

import java.util.List;

public interface BookingService {
    BookingDto initialiseBooking(BookingRequest bookingRequest);

    BookingDto addGuests( Long bookingId ,  List<GuestDto> guestDto);
}
