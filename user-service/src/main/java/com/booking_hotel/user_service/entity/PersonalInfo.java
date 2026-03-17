package com.booking_hotel.user_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Embeddable
@Getter
@NoArgsConstructor
public class PersonalInfo {

    @Column(name = "first_name", nullable = false)
    private String firstname;

    @Column(name = "surname", nullable = false)
    private String surname;

    @Column(name = "birthday", nullable = false)
    private LocalDate birthday;

    public PersonalInfo(String firstname, String surname, LocalDate birthday) {
        this.firstname = firstname;
        this.surname = surname;
        this.birthday = birthday;
    }
}
