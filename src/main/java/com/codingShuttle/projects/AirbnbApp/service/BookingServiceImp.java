package com.codingShuttle.projects.AirbnbApp.service;

import com.codingShuttle.projects.AirbnbApp.dto.BookingDto;
import com.codingShuttle.projects.AirbnbApp.dto.BookingRequest;
import com.codingShuttle.projects.AirbnbApp.dto.GuestDto;
import com.codingShuttle.projects.AirbnbApp.entity.*;
import com.codingShuttle.projects.AirbnbApp.entity.enums.BookingStatus;
import com.codingShuttle.projects.AirbnbApp.exception.ResourceNotFoundException;
import com.codingShuttle.projects.AirbnbApp.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImp implements BookingService{

    private final BookingRepository bookingRepository;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final InventoryRepository inventoryRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final GuestRepository guestRepository;
    @Override
    @Transactional
    public BookingDto initialiseBooking(BookingRequest bookingRequest) {
        try {
            log.info("Initialising booking for hotel : {} , room : {} , date {} - {}  ",
                    bookingRequest.getHotelId(),
                    bookingRequest.getRoomId(),
                    bookingRequest.getCheckInDate(),
                    bookingRequest.getCheckOutDate()
            );
        Hotel hotel = hotelRepository.findById(bookingRequest.getHotelId()).orElseThrow(() ->
                new ResourceNotFoundException("Hotel not found"));

        Room room = roomRepository.findById(bookingRequest.getHotelId()).orElseThrow(() ->
                new ResourceNotFoundException("Room not found"));

        //find the inventories with conditions
        List<Inventory> inventoryList = inventoryRepository.findAndLockAvailableInventory(
                room.getId(),
                bookingRequest.getCheckInDate(),
                bookingRequest.getCheckOutDate(),
                bookingRequest.getRoomCount()
        );


        //count the number of days
        long daysCount = ChronoUnit.DAYS.between(bookingRequest.getCheckInDate() , bookingRequest.getCheckOutDate()) + 1;
        System.out.println("Number of days booking {}" +daysCount);
        System.out.println("Inventory size {} " + inventoryList.size());
        if(inventoryList.size() != daysCount) //we are checking we can get all the inventory for all the days or not
        {
            throw new IllegalStateException("Room is not available anymore");
        }

        //reserve tje room / update the booked count of inventories

        for(Inventory inventory : inventoryList)
        {
           inventory.setReserveCount(inventory.getReserveCount() + bookingRequest.getRoomCount());
        }

        inventoryRepository.saveAll(inventoryList);


        //TODO : calculate dynamic amount

        Booking booking = Booking.builder()
                .bookingStatus(BookingStatus.RESERVED)
                .hotel(hotel)
                .room(room)
                .checkIndate(bookingRequest.getCheckInDate())
                .checkOutDate(bookingRequest.getCheckOutDate())
                .user(getCurrentUser())
                .roomsCount(bookingRequest.getRoomCount())
                .amount(BigDecimal.TEN)
                .build();
            booking = bookingRepository.save(booking);
            return modelMapper.map(booking, BookingDto.class);
        } catch (Exception e) {
            log.error("Error creating booking: ", e);
            throw e;
        }
    }

    @Override
    public BookingDto addGuests(Long bookingId, List<GuestDto> guestDto) {


        log.info("Adding guests for  booking with id : {} ", bookingId);

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new ResourceNotFoundException("Booking not found with id {}" + bookingId));

        if(hasBookingExpired(booking))
        {
            throw new IllegalStateException("Booking has expired");
        }

        if(booking.getBookingStatus() != BookingStatus.RESERVED)
        {
            throw new IllegalStateException("Booking is not in reserved state , cannot add guest");
        }

        for(GuestDto guest : guestDto)
        {
            Guest guestEntity = modelMapper.map(guest, Guest.class);
            guestEntity.setUser(getCurrentUser());
            guestEntity = guestRepository.save(guestEntity);
            booking.getGuests().add(guestEntity);  //add the guest in booking also
        }

        booking.setBookingStatus(BookingStatus.GUESTS_ADDED);
        booking = bookingRepository.save(booking);  //save the booking repo

        return modelMapper.map(booking, BookingDto.class);

    }



    public boolean hasBookingExpired(Booking booking)
    {
        //if the current time is not before the current time so it is expired
        //ex- user created a booking at 11
        // add 10 min to it
        //if the 11:10 is less the current time so booking expired

        return booking.getCreatedAt().plusMinutes(10).isBefore(LocalDateTime.now());
    }


    public User getCurrentUser()
    {
            User newUser = new User();
            newUser.setEmail("test@example.com");
            newUser.setPassword("password");
            newUser.setName("Test User");
            return userRepository.save(newUser);
    }
}
