package com.pentacode.pentacode_middleware.registration.service;

import com.pentacode.pentacode_middleware.email_gateway.EmailProcessor;
import com.pentacode.pentacode_middleware.email_gateway.enu.*;
import com.pentacode.pentacode_middleware.email_gateway.dto.SendEmailRequest;
import com.pentacode.pentacode_middleware.registration.dto.RegisterRequest;
import com.pentacode.pentacode_middleware.registration.dto.RegisterResponse;
import com.pentacode.pentacode_middleware.registration.dto.VerfifyRequest;
import com.pentacode.pentacode_middleware.registration.dto.VerifyResponse;
import com.pentacode.pentacode_middleware.registration.entity.TempUser;
import com.pentacode.pentacode_middleware.registration.entity.User;
import com.pentacode.pentacode_middleware.registration.enums.Gender;
import com.pentacode.pentacode_middleware.registration.enums.Role;
import com.pentacode.pentacode_middleware.registration.repository.TempUserRepository;
import com.pentacode.pentacode_middleware.registration.repository.UserRepository;
import com.pentacode.pentacode_middleware.security.auth.AuthenticationResponse;
import com.pentacode.pentacode_middleware.security.dto.LoggedUser;
import com.pentacode.pentacode_middleware.utility.OTPGenerator;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.pentacode.pentacode_middleware.security.config.JwtService;
import org.springframework.security.core.context.SecurityContextHolder;


import java.time.LocalDateTime;
import java.util.Optional;

@AllArgsConstructor
@Service
public class UserService {
    private static Logger audit = LogManager.getLogger("audit-log");
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private TempUserRepository tempUserRepository;
    @Autowired
    private UserRepository userRepository;
    private EmailProcessor emailProcessor;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public ResponseEntity<RegisterResponse> createUser(RegisterRequest registerRequest, String log) {
        try {
            //user data validation
            if (registerRequest.getEmail() == null || registerRequest.getEmail().isEmpty()) {
                audit.info(log + ",register,error,E1232,email is null or empty");
                return ResponseEntity.ok(RegisterResponse.builder().statusCode("E1232").statusDetails("email is null or empty").build());
            }
            if (registerRequest.getPassword() == null || registerRequest.getPassword().isEmpty()) {
                audit.info(log + ",register,error,E1232,password is null or empty");
                return ResponseEntity.ok(RegisterResponse.builder().statusCode("E1232").statusDetails("password is null or empty").build());
            }
            if (registerRequest.getFirstname() == null || registerRequest.getFirstname().isEmpty()) {
                audit.info(log + ",register,error,E1232,firstname is null or empty");
                return ResponseEntity.ok(RegisterResponse.builder().statusCode("E1232").statusDetails("firstname is null or empty").build());
            }
            if (registerRequest.getLastname() == null || registerRequest.getLastname().isEmpty()) {
                audit.info(log + ",register,error,E1232,lastname is null or empty");
                return ResponseEntity.ok(RegisterResponse.builder().statusCode("E1232").statusDetails("lastname is null or empty").build());
            }
            if (registerRequest.getMobileNumber() == null || registerRequest.getMobileNumber().isEmpty()) {
                audit.info(log + ",register,error,E1232,mobile number is null or empty");
                return ResponseEntity.ok(RegisterResponse.builder().statusCode("E1232").statusDetails("mobile number is null or empty").build());
            }
            if (registerRequest.getGender() == null) {
                audit.info(log + ",register,error,E1232,gender is null");
                return ResponseEntity.ok(RegisterResponse.builder().statusCode("E1232").statusDetails("gender is null").build());
            }
            if (registerRequest.getRole() == null) {
                audit.info(log + ",register,error,E1232,role is null");
                return ResponseEntity.ok(RegisterResponse.builder().statusCode("E1232").statusDetails("role is null").build());
            }
            if (registerRequest.getDateOfBirth() == null || registerRequest.getDateOfBirth().isEmpty()) {
                audit.info(log + ",register,error,E1232,dob is null or empty");
                return ResponseEntity.ok(RegisterResponse.builder().statusCode("E1232").statusDetails("dob is null").build());
            }
            //check user already exists
            Optional<User> existingUser = userRepository.findByEmail(registerRequest.getEmail());
            if (existingUser.isPresent()) {
                audit.info(log + ",register,error,E1356,user already exists");
                return ResponseEntity.ok(RegisterResponse.builder().statusCode("E1356").statusDetails("user already exists").build());
            }
            String initOtp = OTPGenerator.generateOtp(4);
            String otp = passwordEncoder.encode(initOtp);
            LocalDateTime otpExpiryDate = LocalDateTime.now().plusMinutes(30);
            var tempUser = TempUser.builder()
                    .firstname(registerRequest.getFirstname())
                    .lastname(registerRequest.getLastname())
                    .email(registerRequest.getEmail())
                    .role(registerRequest.getRole().equals(Role.PATIENT) ? Role.PATIENT : Role.DOCTOR)
                    .mobileNumber(registerRequest.getMobileNumber())
                    .password(passwordEncoder.encode(registerRequest.getPassword()))
                    .gender(registerRequest.getGender().equals(Gender.FEMALE) ? Gender.FEMALE : Gender.MALE)
                    .dateOfBirth(registerRequest.getDateOfBirth())
                    .otp(otp)
                    .otpExpiryDateTime(otpExpiryDate)
                    .build();
            tempUserRepository.save(tempUser);
            String[] arr = generateOtpEmailSubjectAndBody(tempUser, initOtp);
            SendEmailRequest sendEmailRequest = new SendEmailRequest();
            sendEmailRequest.setTo(registerRequest.getEmail());
            sendEmailRequest.setSubject(arr[0]);
            sendEmailRequest.setBody(arr[1]);
            sendEmailRequest.setEmailPrority(EmailPrority.HIGH);
            sendEmailRequest.setType(EmailType.OTP);
            emailProcessor.sendEmail(sendEmailRequest);
            return ResponseEntity.ok(RegisterResponse.builder().statusCode("S1000").statusDetails("OTP send successfully").build());
        } catch (Exception e) {
            audit.info(log + ",register,error,E4562,system error," + e);
            return ResponseEntity.ok(RegisterResponse.builder().statusCode("E4562").statusDetails("system error").build());
        }
    }

    public ResponseEntity<VerifyResponse> verifyUser(VerfifyRequest verfifyRequest, String log) {
        try {
            //user data validation
            if (verfifyRequest.getEmail() == null || verfifyRequest.getEmail().isEmpty()) {
                audit.info(log + ",verify,error,E1232,email is null or empty");
                return ResponseEntity.ok(VerifyResponse.builder().statusCode("E1232").statusDetails("email is null or empty").build());
            }
            if (verfifyRequest.getOtp() == null || verfifyRequest.getOtp().isEmpty()) {
                audit.info(log + ",verify,error,E1232,otp is null or empty");
                return ResponseEntity.ok(VerifyResponse.builder().statusCode("E1232").statusDetails("otp is null or empty").build());
            }
            Optional<TempUser> existingUser = tempUserRepository.findTop1ByEmailOrderByIdDesc(verfifyRequest.getEmail());
            if (!existingUser.isPresent()) {
                audit.info(log + ",verify,error,E9765,not otp requested user");
                return ResponseEntity.ok(VerifyResponse.builder().statusCode("E9765").statusDetails("not otp requested user").build());
            }
            TempUser tempUser = existingUser.get();
            if (!tempUser.getOtpExpiryDateTime().isAfter(LocalDateTime.now())) {
                audit.info(log + ",verify,error,E6758,expired otp");
                return ResponseEntity.ok(VerifyResponse.builder().statusCode("E6758").statusDetails("expired otp").build());
            }
            if (!passwordEncoder.matches(verfifyRequest.getOtp(), tempUser.getOtp())) {
                audit.info(log + ",verify,error,E7685,invalid otp");
                return ResponseEntity.ok(VerifyResponse.builder().statusCode("E7685").statusDetails("invalid otp").build());
            }
            User user = User.builder()
                    .email(tempUser.getEmail())
                    .firstname(tempUser.getFirstname())
                    .lastname(tempUser.getLastname())
                    .role(tempUser.getRole())
                    .mobileNumber(tempUser.getMobileNumber())
                    .dateOfBirth(tempUser.getDateOfBirth())
                    .password(tempUser.getPassword())
                    .gender(tempUser.getGender())
                    .isEnabled(true)
                    .build();
            userRepository.save(user);
            return ResponseEntity.ok(VerifyResponse.builder().statusCode("S1000")
                    .statusDetails("registration success").build());
        } catch (Exception e) {
            audit.info(log + ",verify,error,E4562,system error," + e);
            return ResponseEntity.ok(VerifyResponse.builder().statusCode("E4562").statusDetails("system error").build());
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

    private String[] generateOtpEmailSubjectAndBody(TempUser tempUser, String otp) {
        String[] arr = new String[2];
        arr[0] = "OTP Verification | Penta Code";
        arr[1] = "Dear " + tempUser.getFirstname() +
                ",\n\nOTP Code is: " + otp +
                "\n[Important - This otp will only valid for 30 minutes since requested time]" +
                "\n\nThank You!\n\nPenta Code,\nA45,\nSample Address1,\nSample Address2,\nSample Address3." +
                "\nMobile: +941234567\nEmail: pentacode@gmail.com\n";
        return arr;
    }
}
