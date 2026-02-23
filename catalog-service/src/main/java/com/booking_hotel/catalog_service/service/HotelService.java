package com.booking_hotel.catalog_service.service;

import com.booking_hotel.catalog_service.dto.hoteldto.HotelCreateDTO;
import com.booking_hotel.catalog_service.dto.hoteldto.HotelResponseDetailsDTO;
import com.booking_hotel.catalog_service.dto.hoteldto.HotelResponseSummaryDTO;
import com.booking_hotel.catalog_service.dto.hoteldto.HotelUpdateDTO;
import com.booking_hotel.catalog_service.entity.StatusHotel;

import java.util.List;

public interface HotelService {
    List<HotelResponseSummaryDTO> getAllHotels(int page, int size);
    HotelResponseDetailsDTO getHotelById(Long hotelId);
    void addHotel(HotelCreateDTO dto);
    void updateHotel(Long hotelId, HotelUpdateDTO dto);
    void deleteHotel(Long hotelId);
    
    List<HotelResponseSummaryDTO> searchHotelsByName(String name, int page, int size);
    List<HotelResponseSummaryDTO> getHotelsByStatus(StatusHotel status, int page, int size);
    List<HotelResponseSummaryDTO> getHotelsByCity(String city, int page, int size);
    List<HotelResponseSummaryDTO> getHotelsByCountry(String country, int page, int size);
    List<HotelResponseSummaryDTO> getHotelsByRating(Integer minRating, Integer maxRating, int page, int size);
    List<HotelResponseSummaryDTO> searchHotels(String name, String city, String country, StatusHotel status, Integer minRating, Integer maxRating, int page, int size);
    List<HotelResponseSummaryDTO> getActiveHotels(int page, int size);
}

