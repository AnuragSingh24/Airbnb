package com.codingShuttle.projects.AirbnbApp.service;

import com.codingShuttle.projects.AirbnbApp.entity.Booking;

public interface CheckOutService {
    String getCheckOutSession(Booking booking, String successUrl , String failureUrl);

}
