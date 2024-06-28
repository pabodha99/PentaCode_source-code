package com.pentacode.pentacode_middleware.registration.dto;

import com.pentacode.pentacode_middleware.security.auth.AuthenticationResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyResponse {
    private String statusCode;
    private String statusDetails;
}
