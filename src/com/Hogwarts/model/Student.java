package com.Hogwarts.model;

public class Student {
    private int id;
    private String admissionNumber;
    private String name;
    private int age;
    private int classNo;
    private String address;
    private String marks;
    private String fatherName;
    private String fatherNumber;
    private String dob;

    public Student() {}

    public Student(int id, String admissionNumber, String name, int age, int classNo, String address, String marks, String fatherName, String fatherNumber, String dob) {
        this.id = id;
        this.admissionNumber = admissionNumber;
        this.name = name;
        this.age = age;
        this.classNo = classNo;
        this.address = address;
        this.marks = marks;
        this.fatherName = fatherName;
        this.fatherNumber = fatherNumber;
        this.dob = dob;
    }

    public Student(String admissionNumber, String name, int age, int classNo, String address, String marks, String fatherName, String fatherNumber, String dob) {
        this(0, admissionNumber, name, age, classNo, address, marks, fatherName, fatherNumber, dob);
    }

    public int getId() { return id; }
    public String getAdmissionNumber() { return admissionNumber; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public int getClassNo() { return classNo; }
    public String getAddress() { return address; }
    public String getMarks() { return marks; }
    public String getFatherName() { return fatherName; }
    public String getFatherNumber() { return fatherNumber; }
    public String getDob() { return dob; }

    public void setId(int id) { this.id = id; }
    public void setAdmissionNumber(String s) { this.admissionNumber = s; }
    public void setName(String s) { this.name = s; }
    public void setAge(int a) { this.age = a; }
    public void setClassNo(int c) { this.classNo = c; }
    public void setAddress(String s) { this.address = s; }
    public void setMarks(String m) { this.marks = m; }
    public void setFatherName(String s) { this.fatherName = s; }
    public void setFatherNumber(String s) { this.fatherNumber = s; }
    public void setDob(String s) { this.dob = s; }
}