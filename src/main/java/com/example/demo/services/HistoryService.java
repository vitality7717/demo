package com.example.demo.services;

import com.example.demo.models.Cart;
import com.example.demo.models.History;
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

public class HistoryService {
    private List<History> histories;
    private Connection connection;

    public HistoryService() {

    }

    public HistoryService(Connection connection) throws SQLException {
        this.connection = connection;
        String sql = "SELECT * FROM history;";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        List<History> h = new ArrayList<>();
        Gson gson = new Gson();
        while (resultSet.next()) {
            String jsonStr = resultSet.getString(3);
            JsonArray jsonArray = JsonParser.parseString(jsonStr).getAsJsonArray();
            List<Product> productList = gson.fromJson(jsonArray, new TypeToken<List<Product>>() {}.getType());
            h.add(new History(resultSet.getLong(1), resultSet.getLong(2), productList, resultSet.getString(4)));
        }
        this.histories = h;
    }

    public void addToHistory(@NonNull long user_id, @NonNull List<Product> cart) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO history (user_id, cart, number_of_order) VALUES (?, ?, ?);");
        String numberOfOrder = "order" + String.format("%s", user_id);
        String productListJson = new Gson().toJson(cart);
        PGobject jsonObject = new PGobject();
        jsonObject.setType("jsonb");
        jsonObject.setValue(productListJson);
        preparedStatement.setLong(1, user_id);
        preparedStatement.setObject(2, jsonObject);
        preparedStatement.setString(3, numberOfOrder);
        preparedStatement.executeUpdate();
    }

    public List<History> getAllHistory(@NonNull int user_id) throws SQLException {
        String sql = "SELECT * FROM history;";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        List<Product> products = new ArrayList<>();
        Gson gson = new Gson();
        List<History> h = new ArrayList<>();
        while (resultSet.next()) {
            String jsonStr = resultSet.getString(3);
            JsonArray jsonArray = JsonParser.parseString(jsonStr).getAsJsonArray();
            List<Product> productList = gson.fromJson(jsonArray, new TypeToken<List<Product>>() {}.getType());
            products.addAll(productList);
            h.add(new History(resultSet.getLong(1), resultSet.getLong(2), products, resultSet.getString(4)));
        }
        return h;
    }

    public List<History> getAllHistoryByLast4(@NonNull String search) throws SQLException {
        String sql = "SELECT * FROM history WHERE number_of_order LIKE '%" + search + "';";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        List<Product> products = new ArrayList<>();
        Gson gson = new Gson();
        List<History> h = new ArrayList<>();
        while (resultSet.next()) {
            String jsonStr = resultSet.getString(3);
            JsonArray jsonArray = JsonParser.parseString(jsonStr).getAsJsonArray();
            List<Product> productList = gson.fromJson(jsonArray, new TypeToken<List<Product>>() {}.getType());
            products.addAll(productList);
            h.add(new History(resultSet.getLong(1), resultSet.getLong(2), products, resultSet.getString(4)));
        }
        return h;
    }

    public List<Product> getHistory(@NonNull int user_id) throws SQLException {
        String sql = "SELECT cart FROM history WHERE user_id = " + user_id + ";";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        List<Product> products = new ArrayList<>();
        Gson gson = new Gson();
        while (resultSet.next()) {
            String jsonStr = resultSet.getString(1);
            JsonArray jsonArray = JsonParser.parseString(jsonStr).getAsJsonArray();
            List<Product> productList = gson.fromJson(jsonArray, new TypeToken<List<Product>>() {}.getType());
            products.addAll(productList);
        }
        return products;
    }

    public void deleteAllHistories() throws SQLException {
        PreparedStatement statement = connection.prepareStatement("DELETE FROM history;");
        statement.executeUpdate();
    }

    public List<History> getHistoryByOrder(@NonNull String order) {

        return histories;
    }
}
