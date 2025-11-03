package com.codingShuttle.projects.AirbnbApp.service;

import com.codingShuttle.projects.AirbnbApp.entity.Room;

public interface InventoryService {

    void intializeRoomForAYear(Room room);

    void deletFutureInventories(Room room);


}
