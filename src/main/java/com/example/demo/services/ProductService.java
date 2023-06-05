package com.example.demo.services;

import com.example.demo.models.Product;
import lombok.NonNull;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class ProductService {
    private List<Product> products;
    private Connection connection;

    public ProductService() {

    }
    public ProductService(Connection connection) throws SQLException {
        this.connection = connection;
        String sql = "SELECT * FROM products;";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        Product product;
        List<Product> p = new ArrayList<>();
        while (resultSet.next()) {
            product = new Product((long) resultSet.getInt(1), resultSet.getString(2),
                    resultSet.getString(3), resultSet.getInt(4), resultSet.getString(5));
            p.add(product);
        }
        this.products = p;
    }

    public List<Product> getAll() {
        return products;
    }

    public Product getBySearch(@NonNull String name) throws SQLException {
        for (Product product : products) {
            if (name.equals(product.getName())) {
                return product;
            }
        }
        return null;
    }

    public Product getById(@NonNull long id) {
        for (Product p : products) {
            if (p.getId().equals(id)) {
                return p;
            }
        }
        return null;
    }

    public void add(@NonNull Product product) throws SQLException {
        String sql_add = "INSERT INTO products (photo, name, price, type) VALUES (?, ?, ?, ?);";
        PreparedStatement pstmt = connection.prepareStatement(sql_add);
        pstmt.setString(1, product.getPhoto());
        pstmt.setString(2, product.getName());
        pstmt.setInt(3, product.getPrice());
        pstmt.setString(4, product.getType());
        pstmt.executeUpdate();
    }

    public List<Product> sorted() {
        List<Product> sortedProducts = products;
        Collections.sort(sortedProducts, Comparator.comparingInt(Product::getPrice));
        return sortedProducts;
    }

    public List<Product> sortedByDescent() {
        List<Product> sortedProducts = products;
        Collections.sort(sortedProducts, Comparator.comparingInt(Product::getPrice).reversed());
        return sortedProducts;
    }

    public List<Product> filterByType(String type) {
        List<Product> filtered = products.stream()
                .filter(product -> product.getType().equals(type))
                .collect(Collectors.toList());
        return filtered;
    }

    public boolean editByName(@NonNull String name, @NonNull Product product) throws SQLException {
        long idx = 0;
        for (Product value : products) {
            if (name.equals(value.getName())) {
                idx = value.getId();
                break;
            }
        }
        if (idx == 0) {
            return false;
        } else {
            String sql = "UPDATE products SET photo=?, name=?, price=?, type=? WHERE id=?";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, product.getPhoto());
            pstmt.setString(2, product.getName());
            pstmt.setInt(3, product.getPrice());
            pstmt.setString(4, product.getType());
            pstmt.setLong(5, idx);
            pstmt.executeUpdate();
            return true;
        }
    }

    public void editById(@NonNull long id, @NonNull Product product) throws SQLException {
        String sql = "UPDATE products SET photo=?, name=?, price=?, type=? WHERE id=?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, product.getPhoto());
        pstmt.setString(2, product.getName());
        pstmt.setInt(3, product.getPrice());
        pstmt.setString(4, product.getType());
        pstmt.setLong(5, id);
        pstmt.executeUpdate();
    }

    public void deleteAll() throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate("DELETE FROM products");
        statement.close();
        connection.close();
    }

    public void deleteById(@NonNull long id) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("DELETE FROM products WHERE id = ?");
        statement.setLong(1, id);
        statement.executeUpdate();
    }

    public void deleteByName(@NonNull String name) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("DELETE FROM products WHERE name = ?");
        statement.setString(1, name);
        statement.executeUpdate();
    }
}
