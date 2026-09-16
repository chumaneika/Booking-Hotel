package com.booking_hotel.catalog_service.repository;

import com.booking_hotel.catalog_service.entity.RoomTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RoomTypeRepository extends JpaRepository<RoomTypeEntity, Long> {
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select r from RoomTypeEntity r where r.id=:id")
    java.util.Optional<RoomTypeEntity> findLockedById(@org.springframework.data.repository.query.Param("id") Long id);

    List<RoomTypeEntity> findRoomTypeByHotelId(Long hotelId);

}
