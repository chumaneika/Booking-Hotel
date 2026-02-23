package com.booking_hotel.catalog_service.service.impl;

import com.booking_hotel.catalog_service.dto.hoteldto.HotelCreateDTO;
import com.booking_hotel.catalog_service.dto.hoteldto.HotelResponseDetailsDTO;
import com.booking_hotel.catalog_service.dto.hoteldto.HotelResponseSummaryDTO;
import com.booking_hotel.catalog_service.dto.hoteldto.HotelUpdateDTO;
import com.booking_hotel.catalog_service.dto.mapper.HotelMapper;
import com.booking_hotel.catalog_service.entity.Address;
import com.booking_hotel.catalog_service.entity.HotelEntity;
import com.booking_hotel.catalog_service.entity.StatusHotel;
import com.booking_hotel.catalog_service.repository.HotelRepository;
import com.booking_hotel.catalog_service.service.HotelService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Primary
public class HotelServiceJpa implements HotelService {

    private final HotelRepository hotelRepository;
    private final HotelMapper hotelMapper;

    @Override
    @Transactional
    public List<HotelResponseSummaryDTO> getAllHotels(int page, int size) {
        PageRequest pageRequest = PageRequest.of(
                page,
                size,
                Sort.by("id").ascending()
        );

        return hotelRepository.findAll(pageRequest).stream()
                .map(hotelMapper::toSummaryDTO)
                .toList();
    }

    @Override
    @Transactional
    public HotelResponseDetailsDTO getHotelById(Long hotelId) {
        HotelEntity hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new EntityNotFoundException("Hotel is not found"));

        return hotelMapper.toDetailsDTO(hotel);

    }

    @Override
    @Transactional
    public void addHotel(HotelCreateDTO dto) {
        Address address = new Address(
                dto.country(),
                dto.city(),
                dto.address()
        );

        HotelEntity hotel = new HotelEntity(
                dto.name(),
                dto.description(),
                1,
                address,
                List.of(),
                dto.status()
        );

        hotelRepository.save(hotel);
    }

    @Override
    @Transactional
    public void updateHotel(Long hotelId, HotelUpdateDTO dto) {
        HotelEntity hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new EntityNotFoundException("Hotel is not found"));

        if (dto.name() != null) {
            hotel.changeName(dto.name());
        }
        if (dto.description() != null) {
            hotel.changeDescription(dto.description());
        }
        if (dto.rating() != null) {
            hotel.changeRating(dto.rating());
        }
        if (dto.status() != null) {
            hotel.changeStatus(dto.status());
        }
        if (dto.country() != null || dto.city() != null || dto.address() != null) {
            Address currentAddress = hotel.getAddress();
            Address newAddress = new Address(
                    dto.country() != null ? dto.country() : currentAddress.getCountry(),
                    dto.city() != null ? dto.city() : currentAddress.getCity(),
                    dto.address() != null ? dto.address() : currentAddress.getAddress()
            );
            // Since Address is @Embedded, we can directly assign new instance
            // Using reflection to set the address field
            try {
                java.lang.reflect.Field addressField = HotelEntity.class.getDeclaredField("address");
                addressField.setAccessible(true);
                addressField.set(hotel, newAddress);
            } catch (Exception e) {
                throw new RuntimeException("Failed to update address", e);
            }
        }

        hotelRepository.save(hotel);
    }

    @Override
    @Transactional
    public void deleteHotel(Long hotelId) {
        HotelEntity hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new EntityNotFoundException("Hotel is not found"));

        if (hotel.getStatus() == StatusHotel.INACTIVE) {
            throw new IllegalStateException("Hotel already has inactive");
        } else {
            hotel.changeStatus(StatusHotel.INACTIVE);
        }

        hotelRepository.save(hotel);
    }

    @Override
    @Transactional
    public List<HotelResponseSummaryDTO> searchHotelsByName(String name, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("id").ascending());
        return hotelRepository.findByNameContainingIgnoreCase(name, pageRequest)
                .stream()
                .map(hotelMapper::toSummaryDTO)
                .toList();
    }

    @Override
    @Transactional
    public List<HotelResponseSummaryDTO> getHotelsByStatus(StatusHotel status, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("id").ascending());
        return hotelRepository.findByStatus(status, pageRequest)
                .stream()
                .map(hotelMapper::toSummaryDTO)
                .toList();
    }

    @Override
    @Transactional
    public List<HotelResponseSummaryDTO> getHotelsByCity(String city, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("id").ascending());
        return hotelRepository.findByCity(city, pageRequest)
                .stream()
                .map(hotelMapper::toSummaryDTO)
                .toList();
    }

    @Override
    @Transactional
    public List<HotelResponseSummaryDTO> getHotelsByCountry(String country, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("id").ascending());
        return hotelRepository.findByCountry(country, pageRequest)
                .stream()
                .map(hotelMapper::toSummaryDTO)
                .toList();
    }

    @Override
    @Transactional
    public List<HotelResponseSummaryDTO> getHotelsByRating(Integer minRating, Integer maxRating, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("id").ascending());
        return hotelRepository.findByRatingBetween(minRating, maxRating, pageRequest)
                .stream()
                .map(hotelMapper::toSummaryDTO)
                .toList();
    }

    @Override
    @Transactional
    public List<HotelResponseSummaryDTO> searchHotels(String name, String city, String country, StatusHotel status, Integer minRating, Integer maxRating, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("id").ascending());
        return hotelRepository.searchHotels(name, city, country, status, minRating, maxRating, pageRequest)
                .stream()
                .map(hotelMapper::toSummaryDTO)
                .toList();
    }

    @Override
    @Transactional
    public List<HotelResponseSummaryDTO> getActiveHotels(int page, int size) {
        return getHotelsByStatus(StatusHotel.ACTIVE, page, size);
    }
}
