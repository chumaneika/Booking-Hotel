package com.booking_hotel.booking_service;

import com.booking_hotel.booking_service.dto.bookingdto.BookingCreateRequestDTO;
import com.booking_hotel.booking_service.dto.bookingroomdto.BookingRoomCreateRequestDTO;
import com.booking_hotel.booking_service.dto.mapper.*;
import com.booking_hotel.booking_service.entity.BookingEntity;
import com.booking_hotel.booking_service.intergration.client.CatalogRoomTypeClient;
import com.booking_hotel.booking_service.intergration.client.dto.RoomTypeDetailsDTO;
import com.booking_hotel.booking_service.kafka.publisher.BookingEventPublisher;
import com.booking_hotel.booking_service.repository.BookingRepository;
import com.booking_hotel.booking_service.security.BookingAccessGuard;
import com.booking_hotel.booking_service.service.impl.BookingServiceJpa;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class BookingPricingTests {
    private BigDecimal catalogPrice=new BigDecimal("5000.00");
    private int saved;
    private final BookingRepository repository=(BookingRepository)Proxy.newProxyInstance(
            BookingRepository.class.getClassLoader(),new Class<?>[]{BookingRepository.class},(proxy,method,args)-> {
                if(method.getName().equals("save")) { saved++; return args[0]; }
                throw new AssertionError("Unexpected repository call: "+method.getName());
            });
    private final CatalogRoomTypeClient catalog=new CatalogRoomTypeClient(null) {
        @Override public RoomTypeDetailsDTO getRoomType(Long hotel,Long room) {
            assertEquals(10L,hotel);
            return new RoomTypeDetailsDTO(room,"STANDARD",2,catalogPrice,25,"DOUBLE",5);
        }
    };
    private final BookingAccessGuard guard=new BookingAccessGuard() {
        @Override public void ensureCurrentUserOrPrivileged(Long user) { assertEquals(1L,user); }
    };
    private final BookingEventPublisher publisher=new BookingEventPublisher(null,null) {
        @Override public void publishBookingCreated(BookingEntity booking) {}
        @Override public void publishRoomReservationRequested(BookingEntity booking) {}
    };
    private final BookingRoomMapper rooms=new BookingRoomMapper();
    private final BookingServiceJpa service=new BookingServiceJpa(repository,new BookingServiceMapper(rooms),rooms,guard,publisher,catalog);
    private BookingCreateRequestDTO request(int nights,List<BookingRoomCreateRequestDTO> items) {
        return new BookingCreateRequestDTO(1L,10L,LocalDate.now().plusDays(1),LocalDate.now().plusDays(1+nights),items);
    }
    @Test void ignoresClientPriceAndNights() {
        var result=service.createBooking(request(3,List.of(new BookingRoomCreateRequestDTO(2L,2,new BigDecimal("0.01"),1))));
        assertEquals(new BigDecimal("30000.00"),result.totalPrice());
        assertEquals(3,result.rooms().get(0).nights());
        assertEquals(catalogPrice,result.rooms().get(0).pricePerNight());
        assertEquals(1,saved);
    }
    @Test void acceptsRequestsWithoutLegacyPriceFields() {
        var result=service.createBooking(request(1,List.of(new BookingRoomCreateRequestDTO(2L,1,null,null))));
        assertEquals(catalogPrice,result.totalPrice());
    }
    @Test void rejectsDuplicateRoomsInvalidDatesAndInvalidCatalogPriceBeforeSave() {
        var room=new BookingRoomCreateRequestDTO(2L,1,null,null);
        assertThrows(ResponseStatusException.class,()->service.createBooking(request(0,List.of(room))));
        assertThrows(ResponseStatusException.class,()->service.createBooking(request(366,List.of(room))));
        assertThrows(ResponseStatusException.class,()->service.createBooking(request(3,List.of(room,room))));
        catalogPrice=BigDecimal.ZERO;
        assertThrows(ResponseStatusException.class,()->service.createBooking(request(3,List.of(room))));
        assertEquals(0,saved);
    }
}
