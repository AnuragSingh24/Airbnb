package com.codingShuttle.projects.AirbnbApp.controller;

import com.codingShuttle.projects.AirbnbApp.dto.RoomDto;
import com.codingShuttle.projects.AirbnbApp.entity.Room;
import com.codingShuttle.projects.AirbnbApp.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/hotels/{hotelId}/rooms")
@RequiredArgsConstructor
public class RoomAdminController {

    private final RoomService roomService;
    @PostMapping
    public ResponseEntity<RoomDto> createNewRoom(@RequestBody RoomDto roomDto , @PathVariable Long hotelId)
    {
        RoomDto room = roomService.createNewRoom(hotelId , roomDto);

        return  new ResponseEntity<>(room , HttpStatus.CREATED);
    }

     @GetMapping
    public ResponseEntity<List<RoomDto>> getAllRoomsInHotel(@PathVariable Long hotelId)
     {
         return ResponseEntity.ok(roomService.getAllRoomsInHotel(hotelId));
     }

    @GetMapping("/{roomId}")
    public ResponseEntity<RoomDto> getRoomById(@PathVariable Long hotelId ,@PathVariable Long roomId)
    {
        return ResponseEntity.ok(roomService.getRoomById(roomId));
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<?> DeleteRoomById(@PathVariable Long hotelId ,@PathVariable Long roomId)
    {
       roomService.deleteRoomById(roomId);
       return ResponseEntity.noContent().build();
    }


}
