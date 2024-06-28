package com.pentacode.pentacode_middleware.security.auth;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import com.pentacode.pentacode_middleware.registration.repository.UserRepository;
import com.pentacode.pentacode_middleware.security.config.JwtService;
import com.pentacode.pentacode_middleware.registration.entity.User;
import com.pentacode.pentacode_middleware.security.dto.LoggedUser;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private static Logger audit = LogManager.getLogger("audit-log");

    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {

        try {
            User user = null;
            Optional<User> existingUser = userRepository.findByEmail(authenticationRequest.getEmail());
            if (!existingUser.isPresent()) {
                audit.info("auth,request,processing,user,not found");
                return null;
            }
            user = existingUser.get();
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authenticationRequest.getEmail(),
                            authenticationRequest.getPassword()
                    )
            );
            var jwtToken = jwtService.generateToken(user);
            var loggedUser = mapLoggedUser(user);
            return AuthenticationResponse.builder()
                    .token(jwtToken)
                    .loggedUser(loggedUser)
                    .statusCode("S1000")
                    .statusDetails("authenticated")
                    .build();
        } catch (Exception e) {
            audit.info("auth,request,from," + authenticationRequest.getEmail() + ",user,not found,error," + e.getMessage());
            return null;
        }
    }

    private LoggedUser mapLoggedUser(User user) {
        LoggedUser loggedUser = new LoggedUser();
        if (user.getFirstname() != null) {
            loggedUser.setFirstname(user.getFirstname());
        }
        if (user.getLastname() != null) {
            loggedUser.setLastname(user.getLastname());
        }
        if (user.getEmail() != null) {
            loggedUser.setEmail(user.getEmail());
        }
        loggedUser.setRole(user.getRole());
        return loggedUser;
    }
}
