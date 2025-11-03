package com.codingShuttle.projects.AirbnbApp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        //these things have to be unique
        uniqueConstraints = @UniqueConstraint(
        name =  "unique_hotel_room_date" ,
        columnNames = {"hotel_id" , "room_id" , "date"}

))

public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id" , nullable = false)
    private Hotel hotel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id" , nullable = false)
    private Room room;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false , columnDefinition ="INTEGER DEFAULT 0")
    //Id the field is not define the default value will be zero
    private  Integer bookedCount;


    @Column(nullable = false)
    private  Integer totalCount;


    @Column(nullable = false , precision = 5 , scale = 2)

    //scale is 2 digit after the decimal
    private BigDecimal surgeFactor;

    @Column(nullable = false , precision = 10 , scale = 2)
    private BigDecimal price; //baseprice * surgefactor

  //we can avoid the join
    @Column(nullable = false)
    private String city;


    @Column(nullable = false)
    private Boolean closed;

    @CreationTimestamp //automatically updated when we create
    private LocalDateTime createdAt;

     @UpdateTimestamp
    private LocalDateTime updatedAt;
}
