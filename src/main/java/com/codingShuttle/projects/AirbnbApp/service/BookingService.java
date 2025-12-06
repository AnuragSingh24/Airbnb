package com.codingShuttle.projects.AirbnbApp.service;

import com.codingShuttle.projects.AirbnbApp.dto.BookingDto;
import com.codingShuttle.projects.AirbnbApp.dto.BookingRequest;
import com.codingShuttle.projects.AirbnbApp.dto.GuestDto;
import com.stripe.model.Event;

import java.util.List;
import java.util.Map;

public interface BookingService {
    BookingDto initialiseBooking(BookingRequest bookingRequest);

    BookingDto addGuests( Long bookingId ,  List<GuestDto> guestDto);

    String intiatePayment(Long bookingId);

    void capturePayment(Event event);

    void  cancelBooking(Long bookingId);

    String getBookingStatus(Long bookingId);
}
