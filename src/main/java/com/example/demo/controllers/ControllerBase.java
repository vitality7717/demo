package com.example.demo.controllers;

import com.example.demo.connection.Connect;
import com.example.demo.jwt.JwtProvider;
import com.example.demo.jwt.JwtResponse;
import com.example.demo.services.AuthService;
import com.example.demo.services.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

import java.sql.Connection;
import java.sql.SQLException;

public class ControllerBase {
    JwtResponse token;
    final String secret = "qBTmv4oXFFR2GwjexDJ4t6fsIUIUhhXqlktXjXdkcyygs8nPVEwMfo29VDRRepYDVV5IkIxBMzr7OEHXEHd37w";
    final Connection connection = new Connect().getConnection();
    final UserService userService;

    {
        try {
            userService = new UserService(connection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    final AuthService authService = new AuthService(userService, new JwtProvider(
            "qBTmv4oXFFR2GwjexDJ4t6fsIUIUhhXqlktXjXdkcyygs8nPVEwMfo29VDRRepYDVV5IkIxBMzr7OEHXEHd37w==",
            "zL1HB3Pch05Avfynovxrf/kpF9O2m4NCWKJUjEp27s9J2jEG3ifiKCGylaZ8fDeoONSTJP/wAzKawB8F9rOMNg=="
    ));

    public ControllerBase() {
    }

    public String decodeToken(String token, String key) {
        try {
            Jws<Claims> claims = Jwts.parser()
                    .setSigningKey(key)
                    .parseClaimsJws(token);
            return claims.getBody().getSubject();
        } catch (Exception e) {
            System.out.println("Invalid token: " + e.getMessage()); // выводим сообщение об ошибке при неудачной попытке декодирования
        }
        return null;
    }
}
