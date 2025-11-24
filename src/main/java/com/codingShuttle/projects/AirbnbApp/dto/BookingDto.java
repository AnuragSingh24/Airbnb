package com.codingShuttle.projects.AirbnbApp.dto;


import com.codingShuttle.projects.AirbnbApp.entity.Guest;
import com.codingShuttle.projects.AirbnbApp.entity.Hotel;
import com.codingShuttle.projects.AirbnbApp.entity.Room;
import com.codingShuttle.projects.AirbnbApp.entity.User;
import com.codingShuttle.projects.AirbnbApp.entity.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class BookingDto {
    private Long id;
    private Integer roomsCount;
    private LocalDate checkIndate;
    private LocalDate checkOutDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private BookingStatus bookingStatus;
    private Set<GuestDto> guests;

}
