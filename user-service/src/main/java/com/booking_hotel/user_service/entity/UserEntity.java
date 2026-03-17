package com.booking_hotel.user_service.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Embedded
    private PersonalInfo personalInfo;

    public UserEntity(PersonalInfo personalInfo, Status status) {
        this.personalInfo = personalInfo;
        this.status = status;
    }

    public void changePersonalInfo(String firstname, String surname, LocalDate birthday) {
        this.personalInfo = new PersonalInfo(firstname, surname, birthday);
    }

    public void changeStatus(Status status) {
        if (status != null) {
            this.status = status;
        } else {
            throw new IllegalArgumentException("User's name is not valid");
        }
    }
}