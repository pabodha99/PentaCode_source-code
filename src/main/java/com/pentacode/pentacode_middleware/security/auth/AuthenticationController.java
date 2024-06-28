package com.pentacode.pentacode_middleware.security.auth;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin
public class AuthenticationController {

    private static Logger audit = LogManager.getLogger("audit-log");

    private final AuthenticationService authenticationService;

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest authenticationRequest
    ) {
        try {
            if (authenticationRequest.getEmail() != null)
                audit.info("auth,request,endpoint,/authenticate,at," + new Date() + "," + authenticationRequest.getEmail());
            else
                audit.info("auth,request,endpoint,/authenticate,at," + new Date());
            if (authenticationRequest.getEmail() != null && authenticationRequest.getPassword() != null) {
                audit.info("auth,request,from," + authenticationRequest.getEmail() + ",at," + LocalDateTime.now());
                var authenticationResponseObj = authenticationService.authenticate(authenticationRequest);

                if (authenticationResponseObj == null) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
                return ResponseEntity.ok(authenticationResponseObj);
            } else {
                audit.info("auth,empty,request,at," + LocalDateTime.now());
            }
        } catch (Exception e) {
            audit.info("auth,request,at," + LocalDateTime.now() + ",error," + e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}
