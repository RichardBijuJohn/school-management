package com.Hogwarts.DAO;
import com.Hogwarts.DBConnection.DBConnection;
import com.Hogwarts.model.Classes;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClassDAO {
    // Fetch all classes
    public List<Classes> getAllClasses() {
        List<Classes> classesList = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT id, Grade, total_strength, class_teacher FROM classes");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Classes c = new Classes(
                        rs.getInt("id"),
                        rs.getString("Grade"),
                        rs.getInt("total_strength"),
                        rs.getString("class_teacher")
                );
                classesList.add(c);
            }
        } catch (SQLException e) {
            System.err.println("[DEBUG] ClassDAO.getAllClasses: SQL error");
            e.printStackTrace();
        }
        return classesList;
    }

    // Add new class
    public boolean addClass(String Grade, int total_Strength, String class_teacher) {
      System.out.println("[DEBUG] addClass called with parameters: Grade=" + Grade + ", total_Strength=" + total_Strength + ", class_teacher=" + class_teacher);    
        String sql = "INSERT INTO classes (Grade, total_strength, class_teacher) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (conn == null) {
                System.err.println("[DEBUG] Connection is null");
                return false;
            }
             System.out.println("[DEBUG] Database connection established.");


            ps.setString(1, Grade);
            ps.setInt(2, total_Strength);
            ps.setString(3, class_teacher);
            System.out.println("[DEBUG] About to execute insert statement…");
            int affected = ps.executeUpdate();
            System.out.println("[DEBUG] Insert executed. Rows affected: " + affected);

            if (affected > 0) {
                System.out.println("[DEBUG] ClassDAO.addClass: Inserted class grade=" + Grade);
                return true;
            } else {
                System.err.println("[DEBUG] ClassDAO.addClass: Insert failed for grade=" + Grade);
                return false;
            }
        } catch (SQLException e) {
            System.err.println("[DEBUG] ClassDAO.addClass: SQL error for grade=" + Grade);
            e.printStackTrace();
            return false;
        }
    }


    // Add updateClass method for updating class info if needed
    public boolean updateClass(int id, String Grade, int total_Strength, String class_teacher) {
        String sql = "UPDATE classes SET Grade=?, total_strength=?, class_teacher=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, Grade);
            ps.setInt(2, total_Strength);
            ps.setString(3, class_teacher);
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

