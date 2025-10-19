package com.Hogwarts.model;

public class Teacher {
    private int id;
    private String name;
    private String gender;
    private String phone_number;
    private String subject;
    private String qualification;
    private Integer classAssigned;
    

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

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

        public String getGender() {
        return gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }



    public String getPhone_number() {
        return phone_number;
    }

    public String getSubject() {
        return subject;
    }

    public String getQualification() {
        return qualification;
    }

    public Integer getClassAssigned() {
        return classAssigned;
    }


    public void setId(int id) {
        this.id = id;
    }

}