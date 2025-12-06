package com.codingShuttle.projects.AirbnbApp.service;

import com.codingShuttle.projects.AirbnbApp.entity.Booking;
import com.codingShuttle.projects.AirbnbApp.entity.User;
import com.codingShuttle.projects.AirbnbApp.repository.BookingRepository;
import com.stripe.model.Customer;
import com.stripe.model.checkout.Session;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckOutServiceImp implements  CheckOutService{
    private final BookingRepository bookingRepository;
    private static final BigDecimal FX_INR_TO_USD = new BigDecimal("0.0120");

    @Override
    public String getCheckOutSession(Booking booking, String successUrl, String failureUrl) {
        log.info("Creating checkout session for booking {}" , booking.getId());
        User user  = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try{

            Customer customer  = Customer.create(
                    CustomerCreateParams.builder()
                            .setEmail(user.getEmail())
                            .setName(user.getName())
                            .build()
            );



            BigDecimal amountInInr = booking.getAmount();          // e.g., ₹4,500.00
            BigDecimal amountInUsd = amountInInr.multiply(FX_INR_TO_USD);

            // Convert to USD cents (smallest unit) with banker’s rounding
            long amountInUsdCents = amountInUsd
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(0, RoundingMode.HALF_UP)
                    .longValue();


            SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setBillingAddressCollection(SessionCreateParams.BillingAddressCollection.REQUIRED)
                .setCustomer(customer.getId())
                .setSuccessUrl(successUrl)
                .setCancelUrl(failureUrl)
                    .setClientReferenceId(String.valueOf(booking.getId()))                 // 👈 set it
                    .putMetadata("bookingId", String.valueOf(booking.getId()))            // 👈 set it on Session
                    .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("usd")                    // <- charge in USD
                                                .setUnitAmount(amountInUsdCents)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName(booking.getHotel().getName() + " : " + booking.getRoom().getType())
                                                                .setDescription("Booking ID :" + booking.getId())
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )

                .build();

            Session session = Session.create(params);
            booking.setPaymentSessionId(session.getId());
            bookingRepository.save(booking);
            log.info("Session created successfully for booking {}", booking.getId());



            return session.getUrl();
        }  catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
        return null;
    }
}
