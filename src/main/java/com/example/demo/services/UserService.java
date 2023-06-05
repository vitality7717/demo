package com.example.demo.services;

import com.example.demo.connection.Connect;
import com.example.demo.models.Product;
import com.example.demo.models.Role;
import com.example.demo.models.User;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.postgresql.util.PGobject;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Getter
//@RequiredArgsConstructor
public class UserService {

    private List<User> users;
    private Connection connection;

    public UserService() {

    }

    public UserService(Connection connection) throws SQLException {
        this.connection = connection;
        String sql = "SELECT * FROM users;";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        User user;
        List<User> u = new ArrayList<>();
        while (resultSet.next()) {
            if (resultSet.getString(5).equals("USER")) {
                user = new User((long) resultSet.getInt(1), resultSet.getString(2),
                        resultSet.getString(3), resultSet.getString(4), Collections.singleton(Role.USER));
            } else {
                user = new User((long) resultSet.getInt(1), resultSet.getString(2),
                        resultSet.getString(3), resultSet.getString(4), Collections.singleton(Role.ADMIN));
            }
            u.add(user);
        }
        this.users = u;
    }

    public User getByLogin(@NonNull String login) throws SQLException {
        for (User user : users) {
            if (login.equals(user.getEmail())) {
                return user;
            }
        }
        return null;
    }

    public void register(@NonNull User user) throws SQLException {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String sql_reg = "INSERT INTO users (email, name, password) VALUES (?, ?, ?);";
        PreparedStatement pstmt = connection.prepareStatement(sql_reg);
        pstmt.setString(1, user.getEmail());
        pstmt.setString(2, user.getName());
        pstmt.setString(3, encoder.encode(user.getPassword()));
        pstmt.executeUpdate();
    }

    public void addCartForUser(@NonNull User user) throws SQLException {
        String sql_cart = "INSERT INTO carts (user_id, cart) VALUES (?, ?);";
        String productListJson = new Gson().toJson(new ArrayList<>());
        PGobject jsonObject = new PGobject();
        jsonObject.setType("jsonb");
        jsonObject.setValue(productListJson);
        PreparedStatement pstmt = connection.prepareStatement(sql_cart);
        pstmt.setLong(1, getByLogin(user.getEmail()).getId());
        pstmt.setObject(2, jsonObject);
        pstmt.executeUpdate();
    }

    public void changeRole(@NonNull String user_email, @NonNull String role) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE users SET role = ? WHERE email = ?;");
        preparedStatement.setString(1, role);
        preparedStatement.setString(2, user_email);
        preparedStatement.executeUpdate();
    }

    public List<User> getAllUsers() throws SQLException {
        String sql = "SELECT * FROM users;";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        User user;
        List<User> u = new ArrayList<>();
        while (resultSet.next()) {
            if (resultSet.getString(5).equals("USER")) {
                user = new User((long) resultSet.getInt(1), resultSet.getString(2),
                        resultSet.getString(3), resultSet.getString(4), Collections.singleton(Role.USER));
            } else {
                user = new User((long) resultSet.getInt(1), resultSet.getString(2),
                        resultSet.getString(3), resultSet.getString(4), Collections.singleton(Role.ADMIN));
            }
            u.add(user);
        }
        return u;
    }
}