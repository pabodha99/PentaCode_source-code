package com.pentacode.pentacode_middleware.registration.dto;

import com.pentacode.pentacode_middleware.registration.enums.Gender;
import com.pentacode.pentacode_middleware.registration.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
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
}
