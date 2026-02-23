package com.booking_hotel.catalog_service.repository;

import com.booking_hotel.catalog_service.entity.RoomTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RoomTypeRepository extends JpaRepository<RoomTypeEntity, Long> {

    List<RoomTypeEntity> findRoomTypeByHotelId(Long hotelId);

}
