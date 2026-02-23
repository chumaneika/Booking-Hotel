package com.booking_hotel.catalog_service.service.impl;

import com.booking_hotel.catalog_service.dto.hoteldto.HotelCreateDTO;
import com.booking_hotel.catalog_service.dto.hoteldto.HotelResponseDetailsDTO;
import com.booking_hotel.catalog_service.dto.hoteldto.HotelResponseSummaryDTO;
import com.booking_hotel.catalog_service.dto.hoteldto.HotelUpdateDTO;
import com.booking_hotel.catalog_service.entity.StatusHotel;
import com.booking_hotel.catalog_service.service.HotelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HotelServiceInMemory implements HotelService {
    @Override
    public List<HotelResponseSummaryDTO> getAllHotels(int page, int size) {
        return List.of();
    }

    @Override
    public HotelResponseDetailsDTO getHotelById(Long hotelId) {
        return null;
    }

    @Override
    public void addHotel(HotelCreateDTO dto) {
        // In-memory implementation - no-op
    }

    @Override
    public void updateHotel(Long hotelId, HotelUpdateDTO dto) {
        // In-memory implementation - no-op
    }

    @Override
    public void deleteHotel(Long hotelId) {
        // In-memory implementation - no-op
    }

    @Override
    public List<HotelResponseSummaryDTO> searchHotelsByName(String name, int page, int size) {
        return List.of();
    }

    @Override
    public List<HotelResponseSummaryDTO> getHotelsByStatus(StatusHotel status, int page, int size) {
        return List.of();
    }

    @Override
    public List<HotelResponseSummaryDTO> getHotelsByCity(String city, int page, int size) {
        return List.of();
    }

    @Override
    public List<HotelResponseSummaryDTO> getHotelsByCountry(String country, int page, int size) {
        return List.of();
    }

    @Override
    public List<HotelResponseSummaryDTO> getHotelsByRating(Integer minRating, Integer maxRating, int page, int size) {
        return List.of();
    }

    @Override
    public List<HotelResponseSummaryDTO> searchHotels(String name, String city, String country, StatusHotel status, Integer minRating, Integer maxRating, int page, int size) {
        return List.of();
    }

    @Override
    public List<HotelResponseSummaryDTO> getActiveHotels(int page, int size) {
        return List.of();
    }
}
