package com.example.demo.connection;

import lombok.Getter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Getter
public class Connect {
    private final Connection connection;

    {
        try {
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/mymaydb", "postgres", "2409907717");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
