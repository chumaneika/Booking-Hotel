package com.booking_hotel.user_service;

import com.booking_hotel.user_service.entity.Status;
import com.booking_hotel.user_service.entity.UserEntity;
import com.booking_hotel.user_service.repository.UserRepository;
import com.booking_hotel.user_service.service.UserServiceJpa;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceApplicationTests {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserServiceJpa userService;

	@Test
	void contextLoads() {
	}

	@Test
	void shouldMarkUserAsDeletedWhenUserExists() {

		// Arrange
		UUID userId = UUID.randomUUID();

		UserEntity user = UserEntity.createWithStatus(Status.ACTIVE);

		when(userRepository.findById(userId))
				.thenReturn(Optional.of(user));

		when(userRepository.save(user))
				.thenReturn(user);

		// Act
		userService.deleteUser(userId);

		// Assert
		assertEquals(Status.DELETED, user.getStatus());

		verify(userRepository).findById(userId);
		verify(userRepository).save(user);
	}

}
