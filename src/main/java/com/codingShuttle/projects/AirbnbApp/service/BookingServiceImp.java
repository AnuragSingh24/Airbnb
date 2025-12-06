package com.codingShuttle.projects.AirbnbApp.service;

import com.codingShuttle.projects.AirbnbApp.dto.BookingDto;
import com.codingShuttle.projects.AirbnbApp.dto.BookingRequest;
import com.codingShuttle.projects.AirbnbApp.dto.GuestDto;
import com.codingShuttle.projects.AirbnbApp.entity.*;
import com.codingShuttle.projects.AirbnbApp.entity.enums.BookingStatus;
import com.codingShuttle.projects.AirbnbApp.exception.ResourceNotFoundException;
import com.codingShuttle.projects.AirbnbApp.exception.UnAuthorisedException;
import com.codingShuttle.projects.AirbnbApp.repository.*;
import com.codingShuttle.projects.AirbnbApp.startegy.PricingService;
import com.fasterxml.jackson.databind.JsonNode;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.model.checkout.Session;
import com.stripe.net.ApiResource;

import com.stripe.model.Event;
import com.stripe.param.RefundCreateParams;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final CheckOutService checkOutService;
    private  final PricingService pricingService;
    @Value("${frontend.url}")
    private String frontendUrl;
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

        //reserve the room / update the booked count of inventories

        inventoryRepository.initBooking(room.getId() , bookingRequest.getCheckInDate() , bookingRequest.getCheckOutDate()
        , bookingRequest.getRoomCount());



        //TODO : calculate dynamic amount
       BigDecimal priceForOneRoom  = pricingService.calculateTotalPrice(inventoryList);
       BigDecimal totalPrice = priceForOneRoom.multiply(BigDecimal.valueOf(bookingRequest.getRoomCount()));

        Booking booking = Booking.builder()
                .bookingStatus(BookingStatus.RESERVED)
                .hotel(hotel)
                .room(room)
                .checkIndate(bookingRequest.getCheckInDate())
                .checkOutDate(bookingRequest.getCheckOutDate())
                .user(getCurrentUser())
                .roomsCount(bookingRequest.getRoomCount())
                .amount(totalPrice)
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

        User user = getCurrentUser();

        if(user.equals(booking.getUser()))
        {
            throw  new UnAuthorisedException("Booking does not belong to this user with id :" + user.getId() );
        }

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
            guestEntity.setUser(user);
            guestEntity = guestRepository.save(guestEntity);
            booking.getGuests().add(guestEntity);  //add the guest in booking also
        }

        booking.setBookingStatus(BookingStatus.GUESTS_ADDED);
        booking = bookingRepository.save(booking);  //save the booking repo

        return modelMapper.map(booking, BookingDto.class);

    }

    @Override
    @Transactional
    public String intiatePayment(Long bookingId) {
    Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id :" + bookingId) );

      User user = getCurrentUser();

        if(user.equals(booking.getUser()))
        {
            throw  new UnAuthorisedException("Booking does not belong to this user with id :" + user.getId() );
        }
        if(hasBookingExpired(booking))
        {
            throw new IllegalStateException("Booking has expired");
        }


        String sessionUrl = checkOutService.getCheckOutSession(
                booking,
                frontendUrl + "payments/success",
                frontendUrl + "payments/failure"
        );

        booking.setBookingStatus(BookingStatus.PAYMENT_PENDING);
        bookingRepository.save(booking);
        return sessionUrl;
    }

        @Override
        @Transactional
        public void capturePayment(Event event) {
               if("checkout.session.completed".equals(event.getType()))
               {
                   Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
                   assert session != null;
                   String bookingIdStr = session.getMetadata().get("bookingId");
                   Long bookingId = Long.valueOf(bookingIdStr);

                   Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + bookingId));
                  System.out.println(booking);


                   booking.setBookingStatus(BookingStatus.CONFIRMED);
                   bookingRepository.save(booking);


                //error in this query look into this
//                   inventoryRepository.findAndLockReservedInventory(booking.getRoom().getId() , booking.getCheckIndate() , booking.getCheckOutDate() ,booking.getRoomsCount());]



                   List<Inventory> lockedRows = inventoryRepository.findAndLockReservedInventory(
                           booking.getRoom().getId(),
                           booking.getCheckIndate(),
                           booking.getCheckOutDate(),
                           booking.getRoomsCount()
                   );
                   log.info("Locked {} inventory rows for room {} from {} to {}",
                           lockedRows.size(), booking.getRoom().getId(), booking.getCheckIndate(), booking.getCheckOutDate());


                   inventoryRepository.ConfirmBooking(booking.getRoom().getId()
                           , booking.getCheckIndate() ,
                           booking.getCheckOutDate()
                ,booking.getRoomsCount());


                log.info("Successfully confirmed the booking for bookingId : {}",booking.getId());


               }else {
                   log.warn("Unhandled event type: {}", event.getType());
               }

        }

    @Override
    @Transactional
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new ResourceNotFoundException("Booking not found with id:" + bookingId)
        );
        User user = getCurrentUser();
        if(user.equals(booking.getUser()))
        {
            throw  new UnAuthorisedException("Booking does not belong to this user with id :" + user.getId() );
        }

        if(booking.getBookingStatus() != BookingStatus.CONFIRMED)
        {
            throw new IllegalStateException("Booking is not in Confirmed state, can be cancel");
        }

        booking.setBookingStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        List<Inventory> lockedRows = inventoryRepository.findAndLockReservedInventory(
                booking.getRoom().getId(),
                booking.getCheckIndate(),
                booking.getCheckOutDate(),
                booking.getRoomsCount()
        );
        log.info("Locked {} inventory rows for room {} from {} to {}",
                lockedRows.size(), booking.getRoom().getId(), booking.getCheckIndate(), booking.getCheckOutDate());


        inventoryRepository.cancelBooking(booking.getRoom().getId()
                , booking.getCheckIndate() ,
                booking.getCheckOutDate()
                ,booking.getRoomsCount());

        //handle the refund
        try {
            Session session = Session.retrieve(booking.getPaymentSessionId());
            RefundCreateParams refundCreateParams = RefundCreateParams.builder()
                    .setPaymentIntent(session.getPaymentIntent())
                    .build();
            Refund.create(refundCreateParams);
        }catch (Exception e)
        {
            log.error("Error refunding payment for bookingId : {} ", booking.getId());
        }
    }

    @Override
    public String getBookingStatus(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new ResourceNotFoundException("Booking not found with id {}" + bookingId));

        User user = getCurrentUser();

        if(user.equals(booking.getUser()))
        {
            throw  new UnAuthorisedException("Booking does not belong to this user with id :" + user.getId() );
        }

        return booking.getBookingStatus().name();
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
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
