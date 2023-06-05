package com.example.demo.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

public class DecodeJWT {
    public static void main(String[] args) {
        try {
            Jws<Claims> claims = Jwts.parser()
                    .setSigningKey("qBTmv4oXFFR2GwjexDJ4t6fsIUIUhhXqlktXjXdkcyygs8nPVEwMfo29VDRRepYDVV5IkIxBMzr7OEHXEHd37w==")
                    .parseClaimsJws("eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhbnRvbkBtYWlsLnJ1IiwiZXhwIjoxNjg0ODc1MjQyLCJyb2xlcyI6WyJVU0VSIl0sIm5hbWUiOiLQkNC90YLQvtC9In0.vZ_EfblfSdlHSRuWc36GQtO4AFAyftP9Z7eXSNQeAqX1kg6e_SpPuLASw_zrSh1MkFYBereYa6jkNvBvhe5dqA");
            System.out.println("Subject: " + claims.getBody().getSubject()); // выводим поле Subject
            System.out.println("Expiration: " + claims.getBody().getExpiration()); // выводим дату истечения срока действия
            System.out.println("Issuer: " + claims.getBody().getIssuer()); // выводим издателя токена
        } catch (Exception e) {
            System.out.println("Invalid token: " + e.getMessage()); // выводим сообщение об ошибке при неудачной попытке декодирования
        }
    }
}
