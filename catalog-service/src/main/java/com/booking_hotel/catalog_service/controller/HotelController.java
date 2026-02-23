package com.booking_hotel.catalog_service.controller;

import com.booking_hotel.catalog_service.dto.hoteldto.HotelCreateDTO;
import com.booking_hotel.catalog_service.dto.hoteldto.HotelResponseDetailsDTO;
import com.booking_hotel.catalog_service.dto.hoteldto.HotelResponseSummaryDTO;
import com.booking_hotel.catalog_service.dto.hoteldto.HotelUpdateDTO;
import com.booking_hotel.catalog_service.entity.StatusHotel;
import com.booking_hotel.catalog_service.service.HotelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hotels")
@RequiredArgsConstructor
public class HotelController {

    private final HotelService hotelService;

    /**
     * Получает список всех отелей с пагинацией
     */
    @GetMapping("/get-all-hotels")
    public ResponseEntity<List<HotelResponseSummaryDTO>> getAllHotels(@RequestParam int page, @RequestParam int size) {
        List<HotelResponseSummaryDTO> response = hotelService.getAllHotels(page, size);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Получает детальную информацию об отеле по его ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<HotelResponseDetailsDTO> getHotelById(@PathVariable("id") Long hotelId) {
        HotelResponseDetailsDTO response = hotelService.getHotelById(hotelId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Создаёт новый отель
     */
    @PostMapping("/add-hotel")
    public ResponseEntity<Void> addHotel(@Valid @RequestBody HotelCreateDTO dto) {
        hotelService.addHotel(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Обновляет информацию об отеле
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateHotel(
            @PathVariable("id") Long hotelId,
            @Valid @RequestBody HotelUpdateDTO dto) {
        hotelService.updateHotel(hotelId, dto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * Деактивирует отель по его ID (переводит в статус INACTIVE)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHotel(@PathVariable("id") Long hotelId) {
        hotelService.deleteHotel(hotelId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * Ищет отели по названию (частичное совпадение, без учета регистра)
     */
    @GetMapping("/search")
    public ResponseEntity<List<HotelResponseSummaryDTO>> searchHotelsByName(
            @RequestParam("name") String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<HotelResponseSummaryDTO> response = hotelService.searchHotelsByName(name, page, size);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Получает список отелей по статусу
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<HotelResponseSummaryDTO>> getHotelsByStatus(
            @PathVariable("status") StatusHotel status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<HotelResponseSummaryDTO> response = hotelService.getHotelsByStatus(status, page, size);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Получает список отелей в указанном городе
     */
    @GetMapping("/city/{city}")
    public ResponseEntity<List<HotelResponseSummaryDTO>> getHotelsByCity(
            @PathVariable("city") String city,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<HotelResponseSummaryDTO> response = hotelService.getHotelsByCity(city, page, size);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Получает список отелей в указанной стране
     */
    @GetMapping("/country/{country}")
    public ResponseEntity<List<HotelResponseSummaryDTO>> getHotelsByCountry(
            @PathVariable("country") String country,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<HotelResponseSummaryDTO> response = hotelService.getHotelsByCountry(country, page, size);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Получает список отелей по диапазону рейтинга
     */
    @GetMapping("/rating")
    public ResponseEntity<List<HotelResponseSummaryDTO>> getHotelsByRating(
            @RequestParam("minRating") Integer minRating,
            @RequestParam("maxRating") Integer maxRating,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<HotelResponseSummaryDTO> response = hotelService.getHotelsByRating(minRating, maxRating, page, size);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Комбинированный поиск отелей по нескольким параметрам одновременно
     */
    @GetMapping("/search/advanced")
    public ResponseEntity<List<HotelResponseSummaryDTO>> searchHotels(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) StatusHotel status,
            @RequestParam(required = false) Integer minRating,
            @RequestParam(required = false) Integer maxRating,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<HotelResponseSummaryDTO> response = hotelService.searchHotels(name, city, country, status, minRating, maxRating, page, size);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Получает список только активных отелей
     */
    @GetMapping("/active")
    public ResponseEntity<List<HotelResponseSummaryDTO>> getActiveHotels(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<HotelResponseSummaryDTO> response = hotelService.getActiveHotels(page, size);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
