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

    private final RoomTypeRepository roomTypeRepository;
    private final HotelRepository hotelRepository;
    private final RoomTypeMapper roomTypeMapper;

    @Override
    public List<RoomTypeResponseSummaryDTO> getAllRoomTypes(Long hotelId) {
        List<RoomTypeEntity> roomTypes = roomTypeRepository.findRoomTypeByHotelId(hotelId);

        if (roomTypes.isEmpty()) {
            throw new IllegalStateException("Hotel does not have any rooms");
            // или 404, если отель не найден — зависит от бизнес-логики
        }

        return roomTypes.stream()
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
                null // quantityRoom can be set later
        );

        roomTypeRepository.save(roomType);
    }

    @Override
    public void updateRoomType(Long hotelId, Long roomTypeId, RoomTypeUpdateDTO dto) {
        RoomTypeEntity roomType = roomTypeRepository.findById(roomTypeId)
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
        // quantityRoom doesn't have a change method, so we'll need to handle it differently
        // For now, we'll skip it or add a setter if needed

        roomTypeRepository.save(roomType);
    }

    @Override
    public void deleteRoomType(Long hotelId, Long roomTypeId) {
        RoomTypeEntity roomType = roomTypeRepository.findById(roomTypeId)
                .orElseThrow(() -> new EntityNotFoundException("Room type is not found"));

        if (!roomType.getHotel().getId().equals(hotelId)) {
            throw new IllegalArgumentException("Room type does not belong to the specified hotel");
        }

        roomTypeRepository.delete(roomType);
    }
}
