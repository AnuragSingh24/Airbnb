package com.codingShuttle.projects.AirbnbApp.controller;


import com.codingShuttle.projects.AirbnbApp.service.BookingService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
//import com.stripe.service.v2.PaymentService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
public class WebhookController {

    private  final BookingService bookingService;

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;


    @PostMapping("/payment")
    public ResponseEntity<Void> capturePayments(@RequestBody String payload , @RequestHeader("Stripe-Signature") String sigHeader)
    {
         try{

             Event event = Webhook.constructEvent(payload,sigHeader,endpointSecret);
             bookingService.capturePayment(event);
             return ResponseEntity.noContent().build();
         } catch (SignatureVerificationException e) {
             throw new RuntimeException(e);
         }
    }


}
