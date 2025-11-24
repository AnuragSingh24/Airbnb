package com.codingShuttle.projects.AirbnbApp.dto;

import com.codingShuttle.projects.AirbnbApp.entity.Hotel;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class HotelInfoDto {

    private HotelDto hotel;

    private List<RoomDto> rooms;


}
