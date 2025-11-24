package com.codingShuttle.projects.AirbnbApp.repository;

import com.codingShuttle.projects.AirbnbApp.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking , Long> {



}
