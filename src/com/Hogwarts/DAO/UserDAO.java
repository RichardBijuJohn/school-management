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


    // Replace existing createUser with a transactional implementation
    public boolean createUser(String username, String password, String role, Integer refId) throws SQLException {
        if (username == null || username.trim().isEmpty()) {
            // nothing to do - caller should handle optional username case
            return false;
        }

        String sql = "INSERT INTO users (username, password, role, ref_id) VALUES (?, ?, ?, ?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {

            boolean origAuto = c.getAutoCommit();
            try {
                c.setAutoCommit(false);

                p.setString(1, username);
                p.setString(2, password);
                p.setString(3, role);
                if (refId == null) p.setNull(4, Types.INTEGER);
                else p.setInt(4, refId);

                int rows = p.executeUpdate();
                c.commit();
                return rows > 0;
            } catch (SQLIntegrityConstraintViolationException ex) {
                // duplicate username or FK violation -> rollback and return false
                try { c.rollback(); } catch (SQLException ignore) {}
                System.err.println("createUser - integrity violation: " + ex.getMessage());
                return false;
            } catch (SQLException ex) {
                try { c.rollback(); } catch (SQLException ignore) {}
                System.err.println("createUser - SQL error: " + ex.getMessage());
                throw ex;
            } finally {
                try { c.setAutoCommit(origAuto); } catch (SQLException ignore) {}
            }
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
}
