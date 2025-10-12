package com.Hogwarts.DAO;

import com.Hogwarts.DBConnection.DBConnection;
import com.Hogwarts.model.Class;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClassDAO {

    // Fetch all classes
    public List<Class> getAllClasses() {
        List<Class> classList = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT id, grade, total_strength, class_teacher FROM class");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Class c = new Class(
                        rs.getInt("id"),
                        rs.getString("grade"),
                        rs.getInt("total_strength"),
                        rs.getString("class_teacher")
                );
                classList.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return classList;
    }

    // Add new class
    public boolean addClass(String grade, int totalStrength, String teacher) {
        String sql = "INSERT INTO class (grade, total_strength, class_teacher) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, grade);
            ps.setInt(2, totalStrength);
            ps.setString(3, teacher);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
