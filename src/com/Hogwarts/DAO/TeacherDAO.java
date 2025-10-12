package com.Hogwarts.DAO;

import java.sql.*;
import java.util.*;
import com.Hogwarts.model.Teacher;
import com.Hogwarts.DBConnection.DBConnection;


public class TeacherDAO {
    public List<Teacher> getAll() throws SQLException {
        List<Teacher> list = new ArrayList<>();
        String sql = "SELECT * FROM teachers";
        try (Connection c=DBConnection.getConnection(); PreparedStatement p=c.prepareStatement(sql); ResultSet rs=p.executeQuery()){
            while (rs.next()) list.add(new Teacher(rs.getInt("id"), rs.getString("name"), rs.getString("subject"), rs.getString("qualification"), rs.getObject("class_assigned")==null?null:rs.getInt("class_assigned")));
        }
        return list;
    }


    public Teacher getById(int id) throws SQLException {
        String sql = "SELECT * FROM teachers WHERE id=?";
        try (Connection c=DBConnection.getConnection(); PreparedStatement p=c.prepareStatement(sql)){ p.setInt(1,id); try(ResultSet rs=p.executeQuery()){ if (rs.next()) return new Teacher(rs.getInt("id"), rs.getString("name"), rs.getString("subject"), rs.getString("qualification"), rs.getObject("class_assigned")==null?null:rs.getInt("class_assigned")); }}
        return null;
    }


    public int add(Teacher t) throws SQLException {
        String sql = "INSERT INTO teachers (name, subject, qualification, class_assigned) VALUES (?, ?, ?, ?)";
        try (Connection c=DBConnection.getConnection(); PreparedStatement p=c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            p.setString(1,t.getName()); p.setString(2,t.getSubject()); p.setString(3,t.getQualification());
            if (t.getClassAssigned()==null) p.setNull(4, Types.INTEGER); else p.setInt(4, t.getClassAssigned());
            p.executeUpdate(); try (ResultSet rs=p.getGeneratedKeys()){ if (rs.next()) return rs.getInt(1); }
        }
        return -1;
    }


    public void update(Teacher t) throws SQLException {
        String sql = "UPDATE teachers SET name=?, subject=?, qualification=?, class_assigned=? WHERE id=?";
        try (Connection c=DBConnection.getConnection(); PreparedStatement p=c.prepareStatement(sql)){
            p.setString(1,t.getName()); p.setString(2,t.getSubject()); p.setString(3,t.getQualification());
            if (t.getClassAssigned()==null) p.setNull(4, Types.INTEGER); else p.setInt(4, t.getClassAssigned());
            p.setInt(5,t.getId()); p.executeUpdate();
        }
    }


    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM teachers WHERE id=?";
        try (Connection c=DBConnection.getConnection(); PreparedStatement p=c.prepareStatement(sql)){ p.setInt(1,id); p.executeUpdate(); }
    }
}