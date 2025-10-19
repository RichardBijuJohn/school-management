package com.Hogwarts.model;

public class Student {
    private int id;
    private String admissionNumber;
    private String name;
    private int age;
    private int classNo;
    private String address;
    private String firstInternal;
    private String secondInternal;
    private String termExam;
    private String fatherName;
    private String fatherNumber;
    private String dob;
    private String gender;
    // Add marks field for compatibility (can be empty or used for legacy)
    private String marks;

    public Student() {}

    public Student(int id, String admissionNumber, String name, int age, int classNo, String address,
                   String firstInternal, String secondInternal, String termExam,
                   String fatherName, String fatherNumber, String dob, String gender) {
        this.id = id;
        this.admissionNumber = admissionNumber;
        this.name = name;
        this.age = age;
        this.classNo = classNo;
        this.address = address;
        this.firstInternal = firstInternal;
        this.secondInternal = secondInternal;
        this.termExam = termExam;
        this.fatherName = fatherName;
        this.fatherNumber = fatherNumber;
        this.dob = dob;
        this.gender = gender;
        this.marks = ""; // default empty
    }

    public Student(String admissionNumber, String name, int age, int classNo, String address,
                   String firstInternal, String secondInternal, String termExam,
                   String fatherName, String fatherNumber, String dob, String gender) {
        this(0, admissionNumber, name, age, classNo, address, firstInternal, secondInternal, termExam, fatherName, fatherNumber, dob, gender);
    }

    public int getId(){return id;}
    public String getAdmissionNumber(){return admissionNumber;}
    public String getName(){return name;}
    public int getAge(){return age;}
    public int getClassNo(){return classNo;}
    public String getAddress(){return address;}
    public String getFatherName() { return fatherName; }
    public String getFatherNumber() { return fatherNumber; }
    public String getDob() { return dob; }
    public String getFirstInternal() { return firstInternal; }
    public String getSecondInternal() { return secondInternal; }
    public String getTermExam() { return termExam; }
    public String getMarks() { return marks; }
    public String getGender() { return gender; }

    public void setId(int id){this.id=id;}
    public void setAdmissionNumber(String s){this.admissionNumber=s;}
    public void setName(String s){this.name=s;}
    public void setAge(int a){this.age=a;}
    public void setClassNo(int c){this.classNo=c;}
    public void setAddress(String s){this.address=s;}
    public void setFatherName(String s) { this.fatherName = s; }
    public void setFatherNumber(String s) { this.fatherNumber = s; }
    public void setDob(String s) { this.dob = s; }
    public void setFirstInternal(String s) { this.firstInternal = s; }
    public void setSecondInternal(String s) { this.secondInternal = s; }
    public void setTermExam(String s) { this.termExam = s; }
    public void setMarks(String m) { this.marks = m; }
    public void setGender(String gender) { this.gender = gender; }
}