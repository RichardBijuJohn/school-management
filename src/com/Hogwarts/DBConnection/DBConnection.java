package com.Hogwarts.DBConnection;
import java.sql.*;


public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost/Hogwarts";
    private static final String USER = "hogwarts_Records";
    private static final String PASS = "Harry@123";


    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Database connected successfully!!");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC driver not found. Add connector to classpath.");
        }
    }


    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}