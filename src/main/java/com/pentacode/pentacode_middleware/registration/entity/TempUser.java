package com.pentacode.pentacode_middleware.registration.entity;

import com.pentacode.pentacode_middleware.registration.enums.Gender;
import com.pentacode.pentacode_middleware.registration.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class TempUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private String mobileNumber;
    private String dateOfBirth;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @Enumerated(EnumType.STRING)
    private Role role;
    private String otp;
    private LocalDateTime otpExpiryDateTime;
}
