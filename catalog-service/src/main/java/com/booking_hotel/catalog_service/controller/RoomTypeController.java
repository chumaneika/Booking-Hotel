package com.booking_hotel.catalog_service.controller;

import com.booking_hotel.catalog_service.dto.roomtypedto.RoomTypeCreateDTO;
import com.booking_hotel.catalog_service.dto.roomtypedto.RoomTypeResponseDetailsDTO;
import com.booking_hotel.catalog_service.dto.roomtypedto.RoomTypeResponseSummaryDTO;
import com.booking_hotel.catalog_service.dto.roomtypedto.RoomTypeUpdateDTO;
import com.booking_hotel.catalog_service.service.RoomTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/room-types")
@RequiredArgsConstructor
public class RoomTypeController {

    private final RoomTypeService roomTypeService;

    /**
     * Получает список всех типов комнат для указанного отеля
     */
    @GetMapping
    public ResponseEntity<List<RoomTypeResponseSummaryDTO>> getAllRoomTypes(@RequestParam("hotelId") Long hotelId) {
        List<RoomTypeResponseSummaryDTO> response = roomTypeService.getAllRoomTypes(hotelId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Получает детальную информацию о типе комнаты по его ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<RoomTypeResponseDetailsDTO> getRoomTypeById(
            @PathVariable("id") Long roomTypeId,
            @RequestParam("hotelId") Long hotelId) {
        RoomTypeResponseDetailsDTO response = roomTypeService.getRoomTypeById(hotelId, roomTypeId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Создает новый тип комнаты для указанного отеля
     */
    @PostMapping
    public ResponseEntity<Void> addRoomType(
            @RequestParam("hotelId") Long hotelId,
            @Valid @RequestBody RoomTypeCreateDTO dto) {
        roomTypeService.addRoomType(hotelId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Обновляет существующий тип комнаты
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateRoomType(
            @PathVariable("id") Long roomTypeId,
            @RequestParam("hotelId") Long hotelId,
            @Valid @RequestBody RoomTypeUpdateDTO dto) {
        roomTypeService.updateRoomType(hotelId, roomTypeId, dto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * Удаляет тип комнаты по его ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoomType(
            @PathVariable("id") Long roomTypeId,
            @RequestParam("hotelId") Long hotelId) {
        roomTypeService.deleteRoomType(hotelId, roomTypeId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
