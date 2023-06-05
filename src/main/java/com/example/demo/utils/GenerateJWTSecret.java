package com.example.demo.utils;

import java.security.SecureRandom;
import java.util.Base64;

public class GenerateJWTSecret {

    public static void main(String[] args) {
        SecureRandom random = new SecureRandom();
        byte[] secretBytes = new byte[32];
        random.nextBytes(secretBytes);
        String secret = Base64.getEncoder().encodeToString(secretBytes);
        System.out.println("JWT_SECRET = " + secret);
    }
}

