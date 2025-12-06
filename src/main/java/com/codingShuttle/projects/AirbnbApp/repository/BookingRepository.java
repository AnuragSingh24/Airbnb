package com.codingShuttle.projects.AirbnbApp.repository;

import com.codingShuttle.projects.AirbnbApp.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Objects;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking , Long> {


    Optional<Booking> findByPaymentSessionId(String sessionId);
}
