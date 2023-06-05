package com.example.demo.controllers;

import com.example.demo.models.History;
import com.example.demo.models.Product;
import com.example.demo.models.Role;
import com.example.demo.models.User;
import com.example.demo.services.HistoryService;
import com.example.demo.services.ProductService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("api/admin")
@NoArgsConstructor
public class AdminController extends ControllerBase {
    /**
     * ЛИЧНЫЙ КАБИНЕТ АДМИНИСТРАТОРА
     */
    @GetMapping("/lc")
    public ResponseEntity<String> lcAdmin(@RequestParam String accessToken) throws SQLException {
        String newToken = decodeToken(accessToken, secret);
        User user = userService.getByLogin(newToken);
        if (Objects.equals(user.getRole(), Collections.singleton(Role.ADMIN))) {
            return ResponseEntity.ok("Hello admin " + user.getName() + "!");
        } else {
            return (ResponseEntity<String>) ResponseEntity.status(403);
        }
    }

    /**
     * ДОБАВИТЬ ТОВАР
     */
    @PostMapping("/lc/allproducts/add")
    public ResponseEntity<Product> addProduct(@RequestParam String accessToken, @RequestBody Product product) throws SQLException {
        String newToken = decodeToken(accessToken, secret);
        User user = userService.getByLogin(newToken);
        if (Objects.equals(user.getRole(), Collections.singleton(Role.ADMIN))) {
            ProductService productService = new ProductService(connection);
            productService.add(product);
            return ResponseEntity.ok(product);
        } else {
            return (ResponseEntity<Product>) ResponseEntity.status(403);
        }
    }

    /**
     * РЕДАКТИРОВАТЬ ТОВАР ПО ИМЕНИ
     */
    @PutMapping("/lc/allproducts/")
    public ResponseEntity<Product> editProductByName(@RequestParam String accessToken, @NonNull String name, @NonNull @RequestBody Product product) throws SQLException {
        String newToken = decodeToken(accessToken, secret);
        User user = userService.getByLogin(newToken);
        if (Objects.equals(user.getRole(), Collections.singleton(Role.ADMIN))) {
            boolean b = new ProductService(connection).editByName(name, product);
            if (b) {
                return ResponseEntity.ok(product);
            }
        }
        return (ResponseEntity<Product>) ResponseEntity.status(403);
    }

    /**
     * РЕДАКТИРОВАТЬ ТОВАР ПО ID
     */
    @PutMapping("/lc/allproducts/{id}")
    public ResponseEntity<Product> editProductById(@RequestParam String accessToken, @PathVariable long id, @NonNull @RequestBody Product product) throws SQLException {
        String newToken = decodeToken(accessToken, secret);
        User user = userService.getByLogin(newToken);
        if (Objects.equals(user.getRole(), Collections.singleton(Role.ADMIN))) {
            new ProductService(connection).editById(id, product);
            return ResponseEntity.ok(product);
        }
        return (ResponseEntity<Product>) ResponseEntity.status(403);
    }

    /**
     * УДАЛИТЬ ТОВАРЫ
     */
    @DeleteMapping("/lc/allproducts")
    public ResponseEntity<List<Product>> deleteAllProducts(@RequestParam String accessToken) throws SQLException {
        String newToken = decodeToken(accessToken, secret);
        User user = userService.getByLogin(newToken);
        if (Objects.equals(user.getRole(), Collections.singleton(Role.ADMIN))) {
            ProductService service = new ProductService(connection);
            service.deleteAll();
            return ResponseEntity.ok(service.getAll());
        }
        return (ResponseEntity<List<Product>>) ResponseEntity.status(403);
    }

    /**
     * УДАЛИТЬ ТОВАР ПО ID
     */
    @DeleteMapping("/lc/allproducts/{id}")
    public ResponseEntity<Product> deleteProductById(@RequestParam String accessToken, @PathVariable long id) throws SQLException {
        String newToken = decodeToken(accessToken, secret);
        User user = userService.getByLogin(newToken);
        if (Objects.equals(user.getRole(), Collections.singleton(Role.ADMIN))) {
            ProductService service = new ProductService(connection);
            Product product = service.getById(id);
            service.deleteById(id);
            return ResponseEntity.ok(product);
        }
        return (ResponseEntity<Product>) ResponseEntity.status(403);
    }

    /**
     * УДАЛИТЬ ТОВАР ПО ИМЕНИ
     */
    @DeleteMapping("/lc/allproducts/")
    public ResponseEntity<Product> deleteProductByName(@RequestParam String accessToken, @RequestParam String name) throws SQLException {
        String newToken = decodeToken(accessToken, secret);
        User user = userService.getByLogin(newToken);
        if (Objects.equals(user.getRole(), Collections.singleton(Role.ADMIN))) {
            ProductService service = new ProductService(connection);
            Product product = service.getBySearch(name);
            service.deleteByName(name);
            return ResponseEntity.ok(product);
        }
        return (ResponseEntity<Product>) ResponseEntity.status(403);
    }

    /**
     * ПОСМОТРЕТЬ ЗАКАЗЫ
     */
    @GetMapping("/lc/orders")
    public ResponseEntity<List<History>> getAllHistoryOrder(@RequestParam String accessToken) throws SQLException {
        String newToken = decodeToken(accessToken, secret);
        User user = userService.getByLogin(newToken);
        if (Objects.equals(user.getRole(), Collections.singleton(Role.ADMIN))) {
            List<History> cartList = new HistoryService(connection).getAllHistory(Math.toIntExact(user.getId()));
            return ResponseEntity.ok(cartList);
        }
        return (ResponseEntity<List<History>>) ResponseEntity.status(403);
    }

    /**
     * ПОСМОТРЕТЬ ЗАКАЗЫ ПО ЧЕТЫРЕМ ПОСЛЕДНИМ БУКВАМ/ЦИФРАМ
     */
    @GetMapping("/lc/orders/")
    public ResponseEntity<List<History>> getAllHistoriesByLastFour(@RequestParam String accessToken, @RequestParam String search) throws SQLException {
        String newToken = decodeToken(accessToken, secret);
        User user = userService.getByLogin(newToken);
        if (Objects.equals(user.getRole(), Collections.singleton(Role.ADMIN))) {
            List<History> cartList = new HistoryService(connection).getAllHistoryByLast4(search);
            return ResponseEntity.ok(cartList);
        }
        return (ResponseEntity<List<History>>) ResponseEntity.status(403);
    }

    /**
     * УДАЛИТЬ ВСЕ ЗАКАЗЫ
     */
    @DeleteMapping("/lc/orders")
    public ResponseEntity<String> deleteAllHistories(@RequestParam String accessToken) throws SQLException {
        String newToken = decodeToken(accessToken, secret);
        User user = userService.getByLogin(newToken);
        if (Objects.equals(user.getRole(), Collections.singleton(Role.ADMIN))) {
            new HistoryService(connection).deleteAllHistories();
            return ResponseEntity.ok("200");
        }
        return (ResponseEntity<String>) ResponseEntity.status(403);
    }

    /**
     * ИЗМЕНИТЬ РОЛЬ ПОЛЬЗОВАТЕЛЯ ПО ЕГО EMAIL
     */
    @PutMapping("/lc/users")
    public ResponseEntity<User> changeRoleOnUser(@RequestParam String accessToken, @RequestParam String user_email, @RequestParam String role) throws SQLException {
        String newToken = decodeToken(accessToken, secret);
        User user = userService.getByLogin(newToken);
        if (Objects.equals(user.getRole(), Collections.singleton(Role.ADMIN))) {
            userService.changeRole(user_email, role);
            User userOnChange = userService.getByLogin(user_email);
            return ResponseEntity.ok(userOnChange);
        }
        return (ResponseEntity<User>) ResponseEntity.status(403);
    }

    /**
     * ПОСМОТРЕТЬ ПОЛЬЗОВАТЕЛЕЙ
     */
    @GetMapping("/lc/getusers")
    public ResponseEntity<List<User>> getUsers(@RequestParam String accessToken) throws SQLException {
        String newToken = decodeToken(accessToken, secret);
        User user = userService.getByLogin(newToken);
        if (Objects.equals(user.getRole(), Collections.singleton(Role.ADMIN))) {
            return ResponseEntity.ok(userService.getUsers());
        }
        return (ResponseEntity<List<User>>) ResponseEntity.status(403);
    }

    /**
     * ВЫЙТИ ИЗ АККАУНТА
     */
    @PostMapping("/lc/logout")
    public ResponseEntity<String> adminLogout(@RequestParam String accessToken) throws SQLException {
        String newToken = decodeToken(accessToken, secret);
        User user = userService.getByLogin(newToken);
        if (Objects.equals(user.getRole(), Collections.singleton(Role.ADMIN))) {
            Claims claims = Jwts.parser()
                    .setSigningKey("qBTmv4oXFFR2GwjexDJ4t6fsIUIUhhXqlktXjXdkcyygs8nPVEwMfo29VDRRepYDVV5IkIxBMzr7OEHXEHd37w==")
                    .parseClaimsJws(accessToken)
                    .getBody();
            claims.put("isExpired", true);
            return ResponseEntity.ok("Logout, admin " + user.getName() + "!");
        }
        return (ResponseEntity<String>) ResponseEntity.status(403);
    }
}