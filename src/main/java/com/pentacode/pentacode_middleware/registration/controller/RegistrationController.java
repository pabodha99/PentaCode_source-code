package com.pentacode.pentacode_middleware.registration.controller;

import com.pentacode.pentacode_middleware.registration.dto.RegisterRequest;
import com.pentacode.pentacode_middleware.registration.dto.RegisterResponse;
import com.pentacode.pentacode_middleware.registration.dto.VerfifyRequest;
import com.pentacode.pentacode_middleware.registration.dto.VerifyResponse;
import com.pentacode.pentacode_middleware.registration.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/register")
public class RegistrationController {

    private static Logger audit = LogManager.getLogger("audit-log");

    @Autowired
    private UserService userService;

    //E4562 -> system error
    //E1232 -> request error
    //E1356 -> user already exists error
    @PostMapping("/otp-request")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest registerRequest, HttpServletRequest request) {
        String log = getMandotaryLogString(request);
        audit.info(log + "," + "register,start,ok");
        if (registerRequest != null) {
            audit.info(log + "," + "register," + registerRequest.getEmail());
            return userService.createUser(registerRequest, log);
        } else {
            audit.info(log + ",register,error,E1232,Request object is null");
            return ResponseEntity.ok(RegisterResponse.builder().statusCode("E1232").statusDetails("Request object is null").build());
        }
    }

    //E4562 -> system error
    //E6758 -> otp expired
    //E7685 -> invalid otp
    //E9765 -> not otp request user
    //E1232 -> request error
    @PostMapping("/otp-verify")
    public ResponseEntity<VerifyResponse> verify(@RequestBody VerfifyRequest verifyError, HttpServletRequest request) {
        String log = getMandotaryLogString(request);
        audit.info(log + "," + "verify,start,ok");
        if (verifyError != null) {
            audit.info(log + "," + "verify," + verifyError.getEmail());
            return userService.verifyUser(verifyError, log);
        } else {
            audit.info(log + ",verify,error,E1232,Request object is null");
            return ResponseEntity.ok(VerifyResponse.builder().statusCode("E1232").statusDetails("Request object is null").build());
        }
    }


    private String getMandotaryLogString(HttpServletRequest httpServletRequest) {
        String sessionId = httpServletRequest.getSession().getId();
        String userAgent = httpServletRequest.getHeader("User-Agent");
        String localIp = httpServletRequest.getLocalAddr();
        String remoteIp = httpServletRequest.getRemoteAddr();
        return sessionId + "," + "|" + userAgent + "|" + "," + localIp + "," + remoteIp + "," + "pentacode-middleware";
    }
}
