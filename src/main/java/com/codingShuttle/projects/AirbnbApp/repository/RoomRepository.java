package com.codingShuttle.projects.AirbnbApp.repository;

import com.codingShuttle.projects.AirbnbApp.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room , Long> {

}
