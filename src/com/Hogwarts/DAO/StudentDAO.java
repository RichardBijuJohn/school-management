package com.Hogwarts.DAO;

import com.Hogwarts.DBConnection.DBConnection;
import com.Hogwarts.model.Student;
import java.sql.*;
import java.util.*;

public class StudentDAO {
    public List<Student> getAll() throws SQLException{
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM students";
        try (Connection c=DBConnection.getConnection();
             PreparedStatement p=c.prepareStatement(sql);
             ResultSet rs=p.executeQuery()){
            while (rs.next())
                list.add(new Student(
                    rs.getInt("id"),
                    rs.getString("admission_number"),
                    rs.getString("name"),
                    rs.getInt("age"),
                    rs.getInt("class_no"),
                    rs.getString("address"),
                    rs.getString("first_internal"),
                    rs.getString("second_internal"),
                    rs.getString("term_exam"),
                    rs.getString("father_name"),
                    rs.getString("father_number"),
                    rs.getString("dob"),
                    rs.getString("gender")
                ));
        }
        return list;
    }


    public List<Student> getByClass(int classNo) throws SQLException{
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM students WHERE class_no=?";
        try (Connection c=DBConnection.getConnection(); PreparedStatement p=c.prepareStatement(sql)){
            p.setInt(1,classNo);
            try(ResultSet rs=p.executeQuery()){
                while (rs.next())
                    list.add(new Student(
                        rs.getInt("id"),
                        rs.getString("admission_number"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        rs.getInt("class_no"),
                        rs.getString("address"),
                        rs.getString("first_internal"),
                        rs.getString("second_internal"),
                        rs.getString("term_exam"),
                        rs.getString("father_name"),
                        rs.getString("father_number"),
                        rs.getString("dob"),
                        rs.getString("gender")
                    ));
            }
        }
        return list;
    }


    public int add(Student s) throws SQLException{
        String sql = "INSERT INTO students (admission_number, name, age, class_no, address, first_internal, second_internal, term_exam, father_name, father_number, dob, gender) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection c=DBConnection.getConnection(); PreparedStatement p=c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            p.setString(1,s.getAdmissionNumber());
            p.setString(2,s.getName());
            p.setInt(3,s.getAge());
            p.setInt(4,s.getClassNo());
            p.setString(5,s.getAddress());
            p.setString(6,s.getFirstInternal());
            p.setString(7,s.getSecondInternal());
            p.setString(8,s.getTermExam());
            p.setString(9,s.getFatherName());
            p.setString(10,s.getFatherNumber());
            p.setString(11,s.getDob());
            p.setString(12,s.getGender());
            p.executeUpdate();
            try(ResultSet rs=p.getGeneratedKeys()){ if (rs.next()) return rs.getInt(1); }
        }
        return -1;
    }


    public void update(Student s) throws SQLException{
        String sql = "UPDATE students SET admission_number=?, name=?, age=?, class_no=?, address=?, first_internal=?, second_internal=?, term_exam=?, father_name=?, father_number=?, dob=?, gender=? WHERE id=?";
        try (Connection c=DBConnection.getConnection(); PreparedStatement p=c.prepareStatement(sql)){
            p.setString(1,s.getAdmissionNumber());
            p.setString(2,s.getName());
            p.setInt(3,s.getAge());
            p.setInt(4,s.getClassNo());
            p.setString(5,s.getAddress());
            p.setString(6,s.getFirstInternal());
            p.setString(7,s.getSecondInternal());
            p.setString(8,s.getTermExam());
            p.setString(9,s.getFatherName());
            p.setString(10,s.getFatherNumber());
            p.setString(11,s.getDob());
            p.setString(12,s.getGender());
            p.setInt(13,s.getId());
            int affected = p.executeUpdate();
            if (affected == 0) {
                System.err.println("[DEBUG] StudentDAO.update: No rows updated for student id=" + s.getId());
            } else {
                System.out.println("[DEBUG] StudentDAO.update: Updated student id=" + s.getId());
            }
        } catch (SQLException ex) {
            System.err.println("[DEBUG] StudentDAO.update: SQL error for student id=" + s.getId());
            ex.printStackTrace();
            throw ex;
        } catch (Exception ex) {
            System.err.println("[DEBUG] StudentDAO.update: General error for student id=" + s.getId());
            ex.printStackTrace();
            throw new SQLException("General error in StudentDAO.update", ex);
        }
    }


    public void delete(int id) throws SQLException{
        String sql = "DELETE FROM students WHERE id=?";
        try (Connection c=DBConnection.getConnection();
             PreparedStatement p=c.prepareStatement(sql)){
            p.setInt(1,id);
            p.executeUpdate(); }
    }


    public int countByClass(int classNo) throws SQLException{
        String sql = "SELECT COUNT(*) FROM students WHERE class_no=?";
        try (Connection c=DBConnection.getConnection(); 
        PreparedStatement p=c.prepareStatement(sql))
        { p.setInt(1,classNo); 
            try(ResultSet rs=p.executeQuery())
            { 
                if (rs.next())
                 return rs.getInt(1); }}
        return 0;
    }
}