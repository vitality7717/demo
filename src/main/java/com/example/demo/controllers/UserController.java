package com.example.demo.controllers;

import com.example.demo.models.*;
import com.example.demo.services.CartService;
import com.example.demo.services.HistoryService;
import com.example.demo.services.ProductService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("api/user")
@NoArgsConstructor
public class UserController extends ControllerBase {
    /**
     * ПОКАЗ ВСЕХ ТОВАРОВ
     */
    @GetMapping("/lc/allproducts")
    public ResponseEntity<List<Product>> getProducts() throws SQLException {
        return ResponseEntity.ok(new ProductService(connection).getAll());
    }

    /**
     * ПОКАЗ ТОВАРА ПО ID
     */
    @GetMapping("/lc/allproducts/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable long id) throws SQLException {
        for (Product p : new ProductService(connection).getAll()) {
            if (p.getId() == id) {
                return ResponseEntity.ok(p);
            }
        }
        return null;
    }

    /**
     * ПОКАЗ КОРЗИНЫ
     */
    @GetMapping("/lc/cart")
    public ResponseEntity<List<Product>> getCart(@RequestParam String accessToken) throws SQLException {
        String newToken = decodeToken(accessToken, secret);
        User user = userService.getByLogin(newToken);
        if (Objects.equals(user.getRole(), Collections.singleton(Role.USER))) {
            List<Product> p = new CartService(connection).getCartByUserId(user.getId());
            return ResponseEntity.ok(p);
        }
        return (ResponseEntity<List<Product>>) ResponseEntity.status(403);
    }

    /**
     * ДОБАВИТЬ В КОРЗИНУ
     */
    @PostMapping("/lc/cart")
    public ResponseEntity<Product> addToCart(@RequestParam String accessToken, @RequestBody Product product) throws SQLException {
        String newToken = decodeToken(accessToken, secret);
        User user = userService.getByLogin(newToken);
        if (Objects.equals(user.getRole(), Collections.singleton(Role.USER))) {
            CartService cartService = new CartService(connection);
            boolean b = cartService.addToCart(user.getId(), product);
            if (b) {
                return ResponseEntity.ok(product);
            }
        }
        return (ResponseEntity<Product>) ResponseEntity.status(403);
    }

    /**
     * УДАЛИТЬ ТОВАР В КОРЗИНЕ ПО ID (БАГАННЫЙ МЕТОД)
     */
    @DeleteMapping("/lc/cart/{id}")
    public ResponseEntity<Product> deleteFromCartById(@RequestParam String accessToken, @PathVariable long id) throws SQLException {
        String newToken = decodeToken(accessToken, secret);
        User user = userService.getByLogin(newToken);
        if (Objects.equals(user.getRole(), Collections.singleton(Role.USER))) {
            CartService cartService = new CartService(connection);
            Product product = new ProductService(connection).getById(id);
            cartService.deleteFromCartById(id);
            return ResponseEntity.ok(product);
        }
        return (ResponseEntity<Product>) ResponseEntity.status(403);
    }

    /**
     * УДАЛИТЬ ИЗ КОРЗИНЫ ТОВАР ПО ИМЕНИ (БАГАННЫЙ МЕТОД)
     */
    @DeleteMapping("/lc/cart/")
    public ResponseEntity<Product> deleteFromCartByName(@RequestParam String accessToken, @RequestParam String name) throws SQLException {
        String newToken = decodeToken(accessToken, secret);
        User user = userService.getByLogin(newToken);
        if (Objects.equals(user.getRole(), Collections.singleton(Role.USER))) {
            CartService cartService = new CartService(connection);
            Product product = new ProductService(connection).getBySearch(name);
            cartService.deleteFromCartByName(name);
            return ResponseEntity.ok(product);
        }
        return (ResponseEntity<Product>) ResponseEntity.status(403);
    }

    /**
     * КУПИТЬ ТОВАР
     */
    @PostMapping("/lc/cart/buy")
    public ResponseEntity<Integer> buyProductsFromCart(@RequestParam String accessToken) throws SQLException {
        String newToken = decodeToken(accessToken, secret);
        User user = userService.getByLogin(newToken);
        if (Objects.equals(user.getRole(), Collections.singleton(Role.USER))) {
            CartService cartService = new CartService(connection);
            HistoryService historyService = new HistoryService(connection);
            int i = 0;
            Cart cart;
            while (i <= cartService.getCountOfCartsByUserId(user.getId())) {
                cart = cartService.getCartsByUserId(user.getId());
                if (cart != null) {
                    historyService.addToHistory(user.getId(), cart.getCart());
                    cartService.buy(user.getId());
                    i++;
                } else {
                    break;
                }
            }
            return ResponseEntity.ok(200);
        }
        return (ResponseEntity<Integer>) ResponseEntity.status(403);
    }

    /**
     * ПОСМОТРЕТЬ ИСТОРИЮ ЗАКАЗОВ
     */
    @GetMapping("/lc/cart/history")
    public ResponseEntity<List<Product>> getHistory(@RequestParam String accessToken) throws SQLException {
        String newToken = decodeToken(accessToken, secret);
        User user = userService.getByLogin(newToken);
        if (Objects.equals(user.getRole(), Collections.singleton(Role.USER))) {
            List<Product> cartList = new HistoryService(connection).getHistory(Math.toIntExact(user.getId()));
            return ResponseEntity.ok(cartList);
        }
        return (ResponseEntity<List<Product>>) ResponseEntity.status(403);
    }

    /**
     * ВЫЙТИ ИЗ АККАУНТА
     */
    @PostMapping("/lc/logout")
    public ResponseEntity<String> userLogout(@RequestParam String accessToken) throws SQLException {
        String newToken = decodeToken(accessToken, secret);
        User user = userService.getByLogin(newToken);
        if (Objects.equals(user.getRole(), Collections.singleton(Role.USER))) {
            Claims claims = Jwts.parser()
                    .setSigningKey("qBTmv4oXFFR2GwjexDJ4t6fsIUIUhhXqlktXjXdkcyygs8nPVEwMfo29VDRRepYDVV5IkIxBMzr7OEHXEHd37w==")
                    .parseClaimsJws(accessToken)
                    .getBody();
            claims.put("isExpired", true);
            return ResponseEntity.ok("Bye-bye, user " + user.getName() + "!");
        }
        return (ResponseEntity<String>) ResponseEntity.status(403);
    }
}