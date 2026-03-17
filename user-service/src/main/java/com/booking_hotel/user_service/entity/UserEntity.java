package com.booking_hotel.user_service.entity;

import com.booking_hotel.user_service.entity.Status;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(
            name = "user_seq",
            sequenceName = "user_seq",
            allocationSize = 10
    )
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    public UserEntity(String name) {
        changeName(name);
        this.status = Status.ACTIVE;
    }

    public void changeName(String name) {
        if (name != null && name.length() > 2 && name.length() <= 50) {
            this.name = name;
        } else {
            throw new IllegalArgumentException("User's name is not valid");
        }
    }

    public void changeStatus(Status status) {
        if (status != null) {
            this.status = status;
        } else {
            throw new IllegalArgumentException("User's name is not valid");
        }
    }
}