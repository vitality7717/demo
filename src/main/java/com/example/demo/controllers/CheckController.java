package com.example.demo.controllers;

import com.example.demo.models.Role;
import com.example.demo.models.User;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Objects;

@RestController
@RequestMapping("api/check")
@NoArgsConstructor
public class CheckController extends ControllerBase {
    /**
     * Привет, пользователь
     */
    @GetMapping("hello/user")
    public ResponseEntity<String> helloUser(String accessToken) throws SQLException {
        String newToken = decodeToken(accessToken, secret);
        User user = userService.getByLogin(newToken);
        if (Objects.equals(user.getRole(), Collections.singleton(Role.USER))) {
            return ResponseEntity.ok("Hello user " + user.getName() + "!");
        }
        return (ResponseEntity<String>) ResponseEntity.status(403);
    }

    /**
     * Привет, админ
     */
    @GetMapping("hello/admin")
    public ResponseEntity<String> helloAdmin(String accessToken) throws SQLException {
        String newToken = decodeToken(accessToken, secret);
        User user = userService.getByLogin(newToken);
        if (Objects.equals(user.getRole(), Collections.singleton(Role.ADMIN))) {
            return ResponseEntity.ok("Hello admin " + user.getName() + "!");
        }
        return (ResponseEntity<String>) ResponseEntity.status(403);
    }

    /**
     * Привет, гость
     */
    @GetMapping("hello/guest")
    public ResponseEntity<String> helloGuest(String accessToken) {
        if (accessToken == null) {
            return ResponseEntity.ok("Hello guest!");
        }
        return (ResponseEntity<String>) ResponseEntity.status(403);
    }
}