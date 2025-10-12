package com.Hogwarts.model;

public class Class {
    private int id;
    private String grade;
    private int totalStrength;
    private String classTeacher;

    public Class(int id, String grade, int totalStrength, String classTeacher) {
        this.id = id;
        this.grade = grade;
        this.totalStrength = totalStrength;
        this.classTeacher = classTeacher;
    }

    // Getters
    public int getId() { return id; }
    public String getGrade() { return grade; }
    public int getTotalStrength() { return totalStrength; }
    public String getClassTeacher() { return classTeacher; }
}
