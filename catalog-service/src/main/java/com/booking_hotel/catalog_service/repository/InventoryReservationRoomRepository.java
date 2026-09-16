package com.booking_hotel.catalog_service.repository;
import com.booking_hotel.catalog_service.entity.InventoryReservationRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.*;
import java.time.LocalDate;
public interface InventoryReservationRoomRepository extends JpaRepository<InventoryReservationRoom,Long> {
    List<InventoryReservationRoom> findByReservationId(UUID reservationId);
    List<InventoryReservationRoom> findByRoomTypeId(Long roomTypeId);
    void deleteByReservationId(UUID reservationId);
    @Query("select coalesce(sum(r.quantity),0) from InventoryReservationRoom r where r.roomTypeId=:id and r.checkInDate<=:day and r.checkOutDate>:day")
    long occupied(@Param("id") Long id,@Param("day") LocalDate day);
}
