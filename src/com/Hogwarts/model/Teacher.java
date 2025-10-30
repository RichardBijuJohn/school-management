package com.Hogwarts.model;

public class Teacher {
    private int id;
    private String name;
    private String gender;
    private String phone_number;
    private String subject;
    private String qualification;
    private Integer classAssigned;
    private String username;
    private String password;

    public Teacher() {}

    public Teacher(int id, String name,String gender, String phone_number, String subject, String qualification, Integer classAssigned) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.phone_number = phone_number;
        this.subject = subject;
        this.qualification = qualification;
        this.classAssigned = classAssigned;
        
    }

    public Teacher(String name,String gender, String phone_number, String subject, String qualification, Integer classAssigned) {
        this(0, name, gender, phone_number, subject, qualification, classAssigned);
    }

    public Teacher(String name, String subject) {
        this.name = name;
        this.subject = subject;
    }

    public int getId() {
        return id;
    }

     public String getName() { return name; }
    public void setName(String name) { this.name = name; }

        public String getGender() {
        return gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone_number() {
        return phone_number;
    }

   public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getQualification() {
        return qualification;
    }

    public Integer getClassAssigned() {
        return classAssigned;
    }


    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
}