package com.example.demo.services;

import com.example.demo.models.Cart;
import com.example.demo.models.Product;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import lombok.NonNull;
import org.postgresql.util.PGobject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CartService {
    private List<Cart> carts;
    private Connection connection;

    public CartService() {

    }

    public CartService(Connection connection) throws SQLException {
        this.connection = connection;
        String sql = "SELECT * FROM carts;";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        List<Cart> c = new ArrayList<>();
        Gson gson = new Gson();
        while (resultSet.next()) {
            String jsonStr = resultSet.getString(3);
            JsonArray jsonArray = JsonParser.parseString(jsonStr).getAsJsonArray();
            List<Product> productList = gson.fromJson(jsonArray, new TypeToken<List<Product>>() {}.getType());
            c.add(new Cart(resultSet.getLong(1), resultSet.getLong(2), productList));
        }
        this.carts = c;
    }

    public List<Cart> getAllCarts() {
        return carts;
    }

    public List<Product> getCartByUserId(@NonNull long id) {
        List<Product> p = new ArrayList<>();
        for (Cart c : carts) {
            if (c.getUser_id() == id) {
                p = c.getCart();
            }
        }
        return p;
    }

    public Cart getCartsByUserId(@NonNull long id) {
        for (Cart c : carts) {
            if (c.getUser_id() == id) {
                return c;
            }
        }
        return null;
    }

    public boolean addToCart(@NonNull long user_id, @NonNull Product product) throws SQLException {
        List<Product> ca = new ArrayList<>();
        for (Cart c : carts) {
            if (Objects.equals(c.getCart(), product)) {
                return false;
            }
            ca = c.getCart();
        }
        ca.add(product);
        String productListJson = new Gson().toJson(ca);
        String sql = "INSERT INTO carts (user_id, cart) VALUES (?, ?);";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        PGobject jsonObject = new PGobject();
        jsonObject.setType("jsonb");
        jsonObject.setValue(productListJson);
        preparedStatement.setLong(1, user_id);
        preparedStatement.setObject(2, jsonObject);
        preparedStatement.executeUpdate();
        return true;
    }

    public void deleteFromCartById(@NonNull long product_id) throws SQLException {
        String sql = "DELETE FROM carts WHERE cart->>id = ?;";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setLong(1, product_id);
        preparedStatement.executeUpdate();
    }

    public void deleteFromCartByName(@NonNull String name) throws SQLException {
        String sql = "DELETE FROM carts WHERE cart->>name = ?;";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, name);
        preparedStatement.executeUpdate();
    }

    public int getCountOfCartsByUserId(@NonNull long user_id) {
        int count = 0;
        for (Cart c : carts) {
            if (user_id == c.getUser_id()) {
                count++;
            }
        }
        return count;
    }

    public void buy(@NonNull long user_id) throws SQLException {
        String sql = "DELETE FROM carts WHERE user_id = ?;";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setLong(1, user_id);
        preparedStatement.executeUpdate();
    }
}
