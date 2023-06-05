package com.example.demo.controllers;

import com.example.demo.jwt.JwtRequest;
import com.example.demo.jwt.JwtResponse;
import com.example.demo.jwt.RefreshJwtRequest;
import com.example.demo.jwt.RegisterRequest;
import com.example.demo.models.Role;
import com.example.demo.models.User;
import com.example.demo.services.UserService;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.message.AuthException;
import java.sql.SQLException;
import java.util.Collections;

@RestController
@RequestMapping("api/auth")
@NoArgsConstructor
public class AuthController extends ControllerBase {
    /**
     * СЕКЦИЯ РЕГИСТРАЦИИ НОВОГО ПОЛЬЗОВАТЕЛЯ
     */
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegisterRequest user) throws SQLException {
        if (userService.getByLogin(user.getEmail()) == null) {
            User newUser = new User(0L, user.getEmail(), user.getName(), user.getPassword(), Collections.singleton(Role.USER));
            userService.register(newUser);
            UserService newUserService = new UserService(connection);
            newUserService.addCartForUser(newUser);
            return ResponseEntity.ok(newUser);
        } else {
            return null;
        }
    }

    /**
     * СЕКЦИЯ АВТОРИЗАЦИИ ДЛЯ ПОЛЬЗОВАТЕЛЕЙ
     */
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest authRequest) throws AuthException, SQLException {
        token = authService.login(authRequest);
        return ResponseEntity.ok(token);
    }

    /**
     * ПОЛУЧЕНИЕ НОВОГО JWT-ТОКЕНА ДЛЯ ПРОДОЛЖЕНИЯ РАБОТЫ (Я НЕ ПОЛЬЗОВАЛСЯ ТАКИМ, ЛУЧШЕ ПРОСТО ВОЙТИ В АККАУНТ ЗАНОВО)
     */
    @PostMapping("/token")
    public ResponseEntity<JwtResponse> getNewAccessToken(@RequestBody RefreshJwtRequest request) throws AuthException, SQLException {
        final JwtResponse token = authService.getAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(token);
    }

    /**
     * ПОЛУЧЕНИЕ НОВОГО ТОКЕНА ДЛЯ ОБНОВЛЕНИЯ (ЛУЧШЕ ПРОСТО ВОЙТИ В АККАУНТ ЗАНОВО)
     */
    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> getNewRefreshToken(@RequestBody RefreshJwtRequest request) throws AuthException, SQLException {
        final JwtResponse token = authService.refresh(request.getRefreshToken());
        return ResponseEntity.ok(token);
    }
}
