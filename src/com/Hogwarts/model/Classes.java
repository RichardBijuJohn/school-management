
package com.Hogwarts.model;

public class Classes {
    private int id;
    private String Grade;
    private int total_Strength;
    private String class_Teacher;

    public Classes(int id, String grade, int totalStrength, String classTeacher) {
        this.id = id;
        this.Grade = grade;
        this.total_Strength = totalStrength;
        this.class_Teacher = classTeacher;
    }

    // Getters
    public int getId() { return id; }
    public String getGrade() { return Grade; }
    public int getTotalStrength() { return total_Strength; }
    public String getClassTeacher() { return class_Teacher; }
}

