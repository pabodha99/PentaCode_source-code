package com.pentacode.pentacode_middleware.utility;

import java.security.SecureRandom;

public class OTPGenerator {
    public static String generateOtp(int length) {
        String canUsedCharacters = "0123456789";
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder otp = new StringBuilder();
        for (int c = 0; c < length; c++) {
            int random = secureRandom.nextInt(canUsedCharacters.length());
            char randomChar = canUsedCharacters.charAt(random);
            otp.append(randomChar);
        }
        return otp.toString();
    }
}

