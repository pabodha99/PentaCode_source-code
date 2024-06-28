package com.pentacode.pentacode_middleware.security.dto;

import com.pentacode.pentacode_middleware.registration.enums.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoggedUser {

    private String firstname;
    private String lastname;
    private String email;
    @Enumerated(EnumType.STRING)
    private Role role;
}
