package com.codingShuttle.projects.AirbnbApp.repository;

import com.codingShuttle.projects.AirbnbApp.entity.Inventory;
import com.codingShuttle.projects.AirbnbApp.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory , Long> {
    void deleteByDateAfterAndRoom(LocalDate date , Room room);
}
