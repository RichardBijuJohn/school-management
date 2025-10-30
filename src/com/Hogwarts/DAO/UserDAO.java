package com.Hogwarts.DAO;
import com.Hogwarts.DBConnection.DBConnection;
import com.Hogwarts.model.User;
import java.sql.*;

public class UserDAO {
    // Fix: Correct method signature for finding by username and password
    public User findByUsernameAndPassword(String username, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE username=? AND password=?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement p = c.prepareStatement(sql)){
            p.setString(1, username); 
            p.setString(2, password);
            try (ResultSet rs = p.executeQuery()){
                if (rs.next()) {
                    return new User(rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("role"),
                            rs.getObject("ref_id") == null ? null : rs.getInt("ref_id"));
                }
            }
        }
        return null;
    }

    // Fix: Ensure commit is done and connection is not closed before insert
    public boolean createUser(String username, String password, String role, Integer refId) throws SQLException {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }

        String sql = "INSERT INTO users (username, password, role, ref_id) VALUES (?, ?, ?, ?)";
        Connection c = null;
        PreparedStatement p = null;
        try {
            c = DBConnection.getConnection();
            c.setAutoCommit(false);
            p = c.prepareStatement(sql);
            p.setString(1, username);
            p.setString(2, password);
            p.setString(3, role);
            if (refId == null) p.setNull(4, Types.INTEGER);
            else p.setInt(4, refId);

            int rows = p.executeUpdate();
            c.commit();
            return rows > 0;
        } catch (SQLIntegrityConstraintViolationException ex) {
            if (c != null) try { c.rollback(); } catch (SQLException ignore) {}
            System.err.println("createUser - integrity violation: " + ex.getMessage());
            return false;
        } catch (SQLException ex) {
            if (c != null) try { c.rollback(); } catch (SQLException ignore) {}
            System.err.println("createUser - SQL error: " + ex.getMessage());
            throw ex;
        } finally {
            if (p != null) try { p.close(); } catch (SQLException ignore) {}
            if (c != null) try { c.setAutoCommit(true); c.close(); } catch (SQLException ignore) {}
        }
    }


    // Add this method to check if a username already exists
    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username=?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement p = c.prepareStatement(sql)){
            p.setString(1, username);
            try (ResultSet rs = p.executeQuery()){
                if (rs.next()) {
                    return new User(rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("role"),
                            rs.getObject("ref_id") == null ? null : rs.getInt("ref_id"));
                }
            }
        }
        return null;
    }

    // Find user by role and ref_id (e.g. TEACHER/STUDENT linked to teacher/student table)
    public User findByRoleAndRefId(String role, int refId) throws SQLException {
        String sql = "SELECT * FROM users WHERE role=? AND ref_id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, role);
            p.setInt(2, refId);
            try (ResultSet rs = p.executeQuery()) {
                if (rs.next()) {
                    return new User(rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("role"),
                            rs.getObject("ref_id") == null ? null : rs.getInt("ref_id"));
                }
            }
        }
        return null;
    }

    // Update username/password for an existing user identified by role+ref_id
    public boolean updateUserCredentials(String role, int refId, String newUsername, String newPassword) throws SQLException {
        if (newUsername == null || newUsername.trim().isEmpty()) return false;
        String sql = "UPDATE users SET username=?, password=? WHERE role=? AND ref_id=?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, newUsername);
            p.setString(2, newPassword);
            p.setString(3, role);
            p.setInt(4, refId);
            int rows = p.executeUpdate();
            return rows > 0;
        }
    }
}
