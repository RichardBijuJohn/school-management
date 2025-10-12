package com.Hogwarts.DAO;

import com.Hogwarts.DBConnection.DBConnection;
import com.Hogwarts.model.Teacher;
import java.sql.*;
import java.util.*;


public class TeacherDAO {
    public List<Teacher> getAll() throws SQLException {
        List<Teacher> list = new ArrayList<>();
        String sql = "SELECT * FROM teachers";
        try (Connection c=DBConnection.getConnection();
             PreparedStatement p=c.prepareStatement(sql);
             ResultSet rs=p.executeQuery()){
            while (rs.next()) list.add(new Teacher(rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("subject"),
                    rs.getString("qualification"),
                    rs.getObject("class_assigned")==null?null:rs.getInt("class_assigned")));
        }
        return list;
    }
    public String getNameByClass(int cls) throws SQLException {
        // SQL to select only the 'name' column where 'class_assigned' matches the input class
        String sql = "SELECT name FROM teachers WHERE class_assigned = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, cls);
            try (ResultSet rs = p.executeQuery()) {
                // Check if a result was found
                if (rs.next()) {
                    // Return the teacher's name
                    return rs.getString("name");
                }}}
        return null;
    }
    public Teacher getById(int id) throws SQLException {
        String sql = "SELECT * FROM teachers WHERE id=?";
        try (Connection c=DBConnection.getConnection();
             PreparedStatement p=c.prepareStatement(sql)){ p.setInt(1,id);
            try(ResultSet rs=p.executeQuery()){
                if (rs.next())
                    return new Teacher(rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("subject"),
                            rs.getString("qualification"),
                            rs.getObject("class_assigned")==null?null:rs.getInt("class_assigned")); }}
        return null;
    }
    public int add(Teacher t) throws SQLException {
        String sql = "INSERT INTO teachers (name, subject, qualification, class_assigned) VALUES (?, ?, ?, ?)";
        try (Connection c=DBConnection.getConnection();
             PreparedStatement p=c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            p.setString(1,t.getName());
            p.setString(2,t.getSubject());
            p.setString(3,t.getQualification());
            if (t.getClassAssigned()==null)
                p.setNull(4, Types.INTEGER);
            else p.setInt(4, t.getClassAssigned());
            p.executeUpdate();
            try (ResultSet rs=p.getGeneratedKeys()){
                if (rs.next()) return rs.getInt(1); }
        }
        return -1;
    }


    public void update(Teacher t) throws SQLException {
        String sql = "UPDATE teachers SET name=?, subject=?, qualification=?, class_assigned=? WHERE id=?";
        try (Connection c=DBConnection.getConnection();
             PreparedStatement p=c.prepareStatement(sql)){
            p.setString(1,t.getName());
            p.setString(2,t.getSubject());
            p.setString(3,t.getQualification());
            if (t.getClassAssigned()==null)
                p.setNull(4, Types.INTEGER);
            else p.setInt(4, t.getClassAssigned());
            p.setInt(5,t.getId()); p.executeUpdate();
        }
    }


    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM teachers WHERE id=?";
        try (Connection c=DBConnection.getConnection();
             PreparedStatement p=c.prepareStatement(sql)){ p.setInt(1,id);
            p.executeUpdate(); }
    }

    public void assignTeacherToClass(String teacherName, int classNo) throws SQLException {
        // Find teacher by name
        String findSql = "SELECT id FROM teachers WHERE name = ?";
        int teacherId = -1;
        try (Connection c = DBConnection.getConnection(); PreparedStatement p = c.prepareStatement(findSql)) {
            p.setString(1, teacherName);
            try (ResultSet rs = p.executeQuery()) {
                if (rs.next()) {
                    teacherId = rs.getInt("id");
                }
            }
        }
        if (teacherId == -1) {
            throw new SQLException("Teacher not found: " + teacherName);
        }
        // Assign class to teacher
        String updateSql = "UPDATE teachers SET class_assigned = ? WHERE id = ?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement p = c.prepareStatement(updateSql)) {
            p.setInt(1, classNo);
            p.setInt(2, teacherId);
            p.executeUpdate();
        }
    }
}