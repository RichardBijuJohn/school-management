package com.Hogwarts.DAO;
import com.Hogwarts.DBConnection.DBConnection;
import com.Hogwarts.model.User;
import java.sql.*;


public class UserDAO {
    public User findByUsernameAndPassword(String username, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE username=? AND password=?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement p = c.prepareStatement(sql)){
            p.setString(1, username); p.setString(2, password);
            try (ResultSet rs = p.executeQuery()){
                if (rs.next()) {
                    return new User(rs.getInt("id"), rs.getString("username"), rs.getString("password"), rs.getString("role"), rs.getObject("ref_id") == null ? null : rs.getInt("ref_id"));
                }
            }
        }
        return null;
    }


    public boolean createUser(String username, String password, String role, Integer refId) throws SQLException {
        String sql = "INSERT INTO users (username, password, role, ref_id) VALUES (?, ?, ?, ?)";
        try (Connection c = DBConnection.getConnection(); PreparedStatement p = c.prepareStatement(sql)){
            p.setString(1, username); p.setString(2, password); p.setString(3, role);
            if (refId==null) p.setNull(4, Types.INTEGER); else p.setInt(4, refId);
            p.executeUpdate();
            return true;
        } catch (SQLIntegrityConstraintViolationException ex) {
            return false; // username exists
        }
    }
}
