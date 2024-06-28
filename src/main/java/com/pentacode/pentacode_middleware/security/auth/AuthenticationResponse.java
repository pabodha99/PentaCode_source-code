package com.pentacode.pentacode_middleware.security.auth;

import com.pentacode.pentacode_middleware.security.dto.LoggedUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {

    private String statusCode;
    private String statusDetails;
    private String token;
    private LoggedUser loggedUser;
}
