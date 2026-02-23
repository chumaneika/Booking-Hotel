package com.booking_hotel.user_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(
            name = "user_seq",
            sequenceName = "user_seq",
            allocationSize = 1
    )
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "passwordHash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    public UserEntity(Long id, String name, String email, String passwordHash, Role role, Status status) {
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = Role.USER;
        this.status = Status.ACTIVE;
    }

    public void changeStatus(Status status) {
        this.status = status;
    }

    public void changePassword(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void changeName(String name) {
        if (name != null && name.length() > 2 && name.length() <= 50) {
            this.name = name;
        } else {
            throw new IllegalArgumentException("User's name is not validate");
        }
    }

}
