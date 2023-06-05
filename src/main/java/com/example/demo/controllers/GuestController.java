package com.example.demo.controllers;

import com.example.demo.models.Product;
import com.example.demo.services.ProductService;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.List;

/**
 * ТУТ КЛАСС КОТОРЫЙ ПОКАЗЫВАЕТ ВСЕМ: И ГОСТЯМ, И АДМИНАМ, И ПОЛЬЗОВАТЕЛЯМ
 */
@RestController
@RequestMapping("api")
@NoArgsConstructor
public class GuestController extends ControllerBase {
    /**
     * ТУТ МЫ ПОЛУЧАЕМ ВСЕ ТОВАРЫ
     */
    @GetMapping("/allproducts")
    public ResponseEntity<List<Product>> getProducts() throws SQLException {
        return ResponseEntity.ok(new ProductService(connection).getAll());
    }

    /**
     * ТУТ МЫ ПОЛУЧАЕМ ТОВАРЫ ПО id
     */
    @GetMapping("/allproducts/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable long id) throws SQLException {
        for (Product p : new ProductService(connection).getAll()) {
            if (p.getId() == id) {
                return ResponseEntity.ok(p);
            }
        }
        return (ResponseEntity<Product>) ResponseEntity.status(500);
    }

    /**
     * ТУТ ПОЛУЧАЕМ ТОВАР ПО ИМЕНИ
     */
    @GetMapping("/allproducts/")
    public ResponseEntity<Product> getProductByName(String name) throws SQLException {
        return ResponseEntity.ok(new ProductService(connection).getBySearch(name));
    }

    /**
     * ТУТ СОРТИРУЕМ ПО ВОЗРАСТАНИЮ
     */
    @GetMapping("/allproducts/sorted")
    public ResponseEntity<List<Product>> getSortedProducts() throws SQLException {
        return ResponseEntity.ok(new ProductService(connection).sorted());
    }

    /**
     * ТУТ СОРТИРУЕМ ПО УБЫВАНИЮ
     */
    @GetMapping("/allproducts/sortedbydescent")
    public ResponseEntity<List<Product>> getSortedByDescentProducts() throws SQLException {
        return ResponseEntity.ok(new ProductService(connection).sortedByDescent());
    }

    /**
     * ФИЛЬТРУЕМ ПО ТИПУ: ВВОДИМ ТИП ТОВАРА И НАМ ВЫВОДЯТСЯ ВСЕ ТОВАРЫ С ТАКИМ ТИПОМ
     */
    @GetMapping("/allproducts/filter")
    public ResponseEntity<List<Product>> getFilterByType(String type) throws SQLException {
        return ResponseEntity.ok(new ProductService(connection).filterByType(type));
    }
}
