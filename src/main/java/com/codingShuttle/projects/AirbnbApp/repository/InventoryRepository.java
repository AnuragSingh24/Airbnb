package com.codingShuttle.projects.AirbnbApp.repository;

import com.codingShuttle.projects.AirbnbApp.entity.Hotel;
import com.codingShuttle.projects.AirbnbApp.entity.Inventory;
import com.codingShuttle.projects.AirbnbApp.entity.Room;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory , Long> {
    void deleteByRoom(Room room);

    //write the query for fetching the details from the inventory

    //criteria
    /*
     city
     startdate and enddate
     closed
     totalcount- bookedCount > roomCount


     grouping the data hotel and room
     datecount

     */
    @Query("""
            SELECT i.hotel FROM Inventory i
            WHERE i.city = :city
              AND i.date  BETWEEN :startDate AND :endDate
              AND i.closed = false
              AND (i.totalCount - i.bookedCount - i.reserveCount) >= :roomCount
            GROUP BY i.hotel
            HAVING COUNT(i.date) = :dateCount
            """)
    Page<Hotel> findHotelsWithAvailableInventory(
            @Param("city") String city,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("roomCount") Integer roomCount,
            @Param("dateCount") Long dateCount,
            Pageable pageable
            );

    @Query("""
    SELECT i FROM Inventory i
    WHERE i.room.id = :roomId
      AND i.date BETWEEN :startDate AND :endDate
      AND i.closed = false
      AND (i.totalCount - i.bookedCount - i.reserveCount) >= :roomCount
    """)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Inventory> findAndLockAvailableInventory(
            @Param("roomId") Long roomId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("roomCount") Integer roomCount
    );


    @Query("""
    SELECT i FROM Inventory i
    WHERE i.room.id = :roomId
      AND i.date BETWEEN :startDate AND :endDate
      AND (i.totalCount - i.bookedCount) >= :numberOfRooms
      AND i.closed = false

    """)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Inventory> findAndLockReservedInventory(
            @Param("roomId") Long roomId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("numberOfRooms") Integer numberOfRooms
    );

    @Modifying
    @Query("""
    UPDATE Inventory i
    SET i.reserveCount = i.reserveCount - :numberOfRooms,
        i.bookedCount = i.bookedCount + :numberOfRooms
    WHERE i.room.id = :roomId
        AND i.date BETWEEN :startDate AND :endDate
          AND (i.totalCount - i.bookedCount) >= :numberOfRooms
          AND i.reserveCount >= : numberOfRooms
      AND i.closed = false
    
    """)
    void ConfirmBooking(
            @Param("roomId") Long roomId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("numberOfRooms") int numberOfRooms
    );

    @Modifying
    @Query("""
            UPDATE Inventory i
            SET i.bookedCount = i.bookedCount - :numberOfRooms
            WHERE i.room.id = :roomId
                AND i.date BETWEEN :startDate AND :endDate
                AND (i.totalCount - i.bookedCount) >= :numberOfRooms
                AND i.closed = false
            """)
    void cancelBooking( @Param("roomId") Long roomId,
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate,
                        @Param("numberOfRooms") int numberOfRooms);


    @Modifying
    @Query("""
            UPDATE Inventory i
            SET i.reserveCount = i.reserveCount + :numberOfRooms
            WHERE i.room.id = :roomId
                AND i.date BETWEEN :startDate AND :endDate
                AND (i.totalCount - i.bookedCount - i.reserveCount) >= :numberOfRooms
                AND i.closed = false
            
           
            """)
    void initBooking( @Param("roomId") Long roomId,
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate,
                        @Param("numberOfRooms") int numberOfRooms);
    List<Inventory> findAllByHotelAndDateBetween(Hotel hotel, LocalDate startDate, LocalDate endDate);
}
