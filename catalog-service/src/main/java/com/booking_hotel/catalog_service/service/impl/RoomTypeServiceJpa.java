package com.booking_hotel.catalog_service.service.impl;

import com.booking_hotel.catalog_service.dto.mapper.RoomTypeMapper;
import com.booking_hotel.catalog_service.dto.roomtypedto.RoomTypeCreateDTO;
import com.booking_hotel.catalog_service.dto.roomtypedto.RoomTypeResponseDetailsDTO;
import com.booking_hotel.catalog_service.dto.roomtypedto.RoomTypeResponseSummaryDTO;
import com.booking_hotel.catalog_service.dto.roomtypedto.RoomTypeUpdateDTO;
import com.booking_hotel.catalog_service.entity.HotelEntity;
import com.booking_hotel.catalog_service.entity.RoomTypeEntity;
import com.booking_hotel.catalog_service.repository.HotelRepository;
import com.booking_hotel.catalog_service.repository.RoomTypeRepository;
import com.booking_hotel.catalog_service.service.RoomTypeService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Primary
@Transactional
public class RoomTypeServiceJpa implements RoomTypeService {
    private final com.booking_hotel.catalog_service.repository.InventoryReservationRoomRepository inventory;

    private final RoomTypeRepository roomTypeRepository;
    private final HotelRepository hotelRepository;
    private final RoomTypeMapper roomTypeMapper;

    @Override
    public List<RoomTypeResponseSummaryDTO> getAllRoomTypes(Long hotelId) {
        if (!hotelRepository.existsById(hotelId)) {
            throw new EntityNotFoundException("Hotel is not found");
        }

        return roomTypeRepository.findRoomTypeByHotelId(hotelId).stream()
                .map(roomTypeMapper::toSummaryDTO)
                .toList();
    }

    @Override
    public RoomTypeResponseDetailsDTO getRoomTypeById(Long hotelId, Long roomTypeId) {
        RoomTypeEntity roomType = roomTypeRepository.findById(roomTypeId)
                .orElseThrow(() -> new EntityNotFoundException("Room type is not found"));

        if (!roomType.getHotel().getId().equals(hotelId)) {
            throw new IllegalArgumentException("Room type does not belong to the specified hotel");
        }

        return roomTypeMapper.toDetailsDTO(roomType);
    }

    @Override
    public void addRoomType(Long hotelId, RoomTypeCreateDTO dto) {
        HotelEntity hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new EntityNotFoundException("Hotel is not found"));

        if (!dto.hotelId().equals(hotelId)) {
            throw new IllegalArgumentException("Hotel ID in DTO does not match the path parameter");
        }

        RoomTypeEntity roomType = new RoomTypeEntity(
                hotel,
                dto.bedType(),
                dto.capacity(),
                dto.name(),
                dto.basePrice(),
                dto.sizeSqm(),
                dto.quantityRoom()
        );

        roomTypeRepository.save(roomType);
    }

    @Override
    public void updateRoomType(Long hotelId, Long roomTypeId, RoomTypeUpdateDTO dto) {
        RoomTypeEntity roomType = roomTypeRepository.findLockedById(roomTypeId)
                .orElseThrow(() -> new EntityNotFoundException("Room type is not found"));

        if (!roomType.getHotel().getId().equals(hotelId)) {
            throw new IllegalArgumentException("Room type does not belong to the specified hotel");
        }

        if (dto.name() != null) {
            roomType.changeRoomType(dto.name());
        }
        if (dto.capacity() != null) {
            roomType.changeCapacity(dto.capacity());
        }
        if (dto.basePrice() != null) {
            roomType.changeBasePrice(dto.basePrice());
        }
        if (dto.sizeSqm() != null) {
            roomType.changeSizeSqm(dto.sizeSqm());
        }
        if (dto.bedType() != null) {
            roomType.changeBedType(dto.bedType());
        }
        if (dto.quantityRoom() != null) {
            var reservations=inventory.findByRoomTypeId(roomTypeId);
            var today=java.time.LocalDate.now();
            var days=new java.util.HashSet<java.time.LocalDate>(); days.add(today);
            reservations.stream().map(com.booking_hotel.catalog_service.entity.InventoryReservationRoom::getCheckInDate)
                    .filter(day->!day.isBefore(today)).forEach(days::add);
            for(var day:days) {
                long occupied=reservations.stream().filter(r->!r.getCheckInDate().isAfter(day) && r.getCheckOutDate().isAfter(day))
                        .mapToLong(com.booking_hotel.catalog_service.entity.InventoryReservationRoom::getQuantity).sum();
                if(occupied>dto.quantityRoom()) throw new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.CONFLICT,"Capacity is below existing reservations");
            }
            roomType.changeQuantityRoom(dto.quantityRoom());
        }

        roomTypeRepository.save(roomType);
    }

    @Override
    public void deleteRoomType(Long hotelId, Long roomTypeId) {
        RoomTypeEntity roomType = roomTypeRepository.findLockedById(roomTypeId)
                .orElseThrow(() -> new EntityNotFoundException("Room type is not found"));

        if (!roomType.getHotel().getId().equals(hotelId)) {
            throw new IllegalArgumentException("Room type does not belong to the specified hotel");
        }

        if(inventory.findByRoomTypeId(roomTypeId).stream().anyMatch(r->r.getCheckOutDate().isAfter(java.time.LocalDate.now())))
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.CONFLICT,"Room type has active reservations");
        roomTypeRepository.delete(roomType);
    }
}
