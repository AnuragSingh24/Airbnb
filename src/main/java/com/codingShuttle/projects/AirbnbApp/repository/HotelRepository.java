package com.codingShuttle.projects.AirbnbApp.repository;

import com.codingShuttle.projects.AirbnbApp.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelRepository extends JpaRepository<Hotel , Long> {
}
