package com.codingShuttle.projects.AirbnbApp.repository;

import com.codingShuttle.projects.AirbnbApp.entity.Guest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestRepository extends JpaRepository<Guest, Long> {
}
