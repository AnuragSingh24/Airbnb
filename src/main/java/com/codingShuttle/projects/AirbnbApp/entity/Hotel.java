package com.codingShuttle.projects.AirbnbApp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "hotel")
public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;


    @Column(nullable = false)  //it cant be null
    private String name;

    private String city;


    @Column(columnDefinition = "TEXT[]") //we can store the array of text here
    private  String[] photos;

    @Column(columnDefinition = "TEXT[]")
    private String[] amenities;

    @CreationTimestamp //automatically updated when we create
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Embedded  ///this we can emmbed the data of hotel info in the class
    private HotelContactInfo contactInfo;


    @Column(nullable = false)
    private Boolean active;

    @ManyToOne
    private User owner;


    @OneToMany(mappedBy = "hotel")
    private List<Room> rooms;

}
