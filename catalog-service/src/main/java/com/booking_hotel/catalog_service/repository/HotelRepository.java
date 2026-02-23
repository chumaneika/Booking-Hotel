package com.booking_hotel.catalog_service.repository;

import com.booking_hotel.catalog_service.entity.HotelEntity;
import com.booking_hotel.catalog_service.entity.StatusHotel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface HotelRepository extends JpaRepository<HotelEntity, Long> {
    
    Page<HotelEntity> findByStatus(StatusHotel status, Pageable pageable);
    
    @Query("SELECT h FROM HotelEntity h WHERE LOWER(h.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<HotelEntity> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);
    
    @Query("SELECT h FROM HotelEntity h WHERE h.address.city = :city")
    Page<HotelEntity> findByCity(@Param("city") String city, Pageable pageable);
    
    @Query("SELECT h FROM HotelEntity h WHERE h.address.country = :country")
    Page<HotelEntity> findByCountry(@Param("country") String country, Pageable pageable);
    
    @Query("SELECT h FROM HotelEntity h WHERE h.rating >= :minRating AND h.rating <= :maxRating")
    Page<HotelEntity> findByRatingBetween(@Param("minRating") Integer minRating, @Param("maxRating") Integer maxRating, Pageable pageable);
    
    @Query("SELECT h FROM HotelEntity h WHERE " +
           "(:name IS NULL OR LOWER(h.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:city IS NULL OR h.address.city = :city) AND " +
           "(:country IS NULL OR h.address.country = :country) AND " +
           "(:status IS NULL OR h.status = :status) AND " +
           "(:minRating IS NULL OR h.rating >= :minRating) AND " +
           "(:maxRating IS NULL OR h.rating <= :maxRating)")
    Page<HotelEntity> searchHotels(
            @Param("name") String name,
            @Param("city") String city,
            @Param("country") String country,
            @Param("status") StatusHotel status,
            @Param("minRating") Integer minRating,
            @Param("maxRating") Integer maxRating,
            Pageable pageable
    );
}
