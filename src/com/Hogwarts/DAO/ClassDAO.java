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
            System.err.println("[DEBUG] ClassDAO.getAllClasses: SQL error");
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
            if (affected > 0) {
                System.out.println("[DEBUG] ClassDAO.addClass: Inserted class grade=" + grade);
                return true;
            } else {
                System.err.println("[DEBUG] ClassDAO.addClass: Insert failed for grade=" + grade);
                return false;
            }
        } catch (SQLException e) {
            System.err.println("[DEBUG] ClassDAO.addClass: SQL error for grade=" + grade);
            e.printStackTrace();
            return false;
        }
    }

    // Add updateClass method for updating class info if needed
    public boolean updateClass(int id, String grade, int totalStrength, String teacher) {
        String sql = "UPDATE class SET grade=?, total_strength=?, class_teacher=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, grade);
            ps.setInt(2, totalStrength);
            ps.setString(3, teacher);
            ps.setInt(4, id);
            int affected = ps.executeUpdate();
            if (affected == 0) {
                System.err.println("[DEBUG] ClassDAO.updateClass: No rows updated for id=" + id);
                return false;
            } else {
                System.out.println("[DEBUG] ClassDAO.updateClass: Updated class id=" + id);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[DEBUG] ClassDAO.updateClass: SQL error for id=" + id);
            e.printStackTrace();
            return false;
        }
    }
}
