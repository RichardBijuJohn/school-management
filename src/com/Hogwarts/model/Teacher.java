package com.Hogwarts.model;

public class Teacher {
    private int id;
    private String name;
    private String subject;
    private String qualification;
    private Integer classAssigned;
    public Teacher() {}
    public Teacher(int id, String name, String subject, String qualification, Integer classAssigned){ this.id=id;this.name=name;this.subject=subject;this.qualification=qualification;this.classAssigned=classAssigned; }
    public Teacher(String name, String subject, String qualification, Integer classAssigned){ this(0,name,subject,qualification,classAssigned); }
    public int getId(){return id;}
    public String getName(){return name;}
    public String getSubject(){return subject;}
    public String getQualification(){return qualification;}
    public Integer getClassAssigned(){return classAssigned;}
    public void setId(int id){this.id=id;}
    public void setName(String s){this.name=s;}
    public void setSubject(String s){this.subject=s;}
    public void setQualification(String s){this.qualification=s;}
    public void setClassAssigned(Integer c){this.classAssigned=c;}
}
