package com.Hogwarts.model;
public class User {
    private int id;
    private String username;
    private String password;
    private String role; // ADMIN, TEACHER, STUDENT
    private Integer refId;


    public User() {}
    public User(int id, String username, String password, String role, Integer refId) {
        this.id = id; this.username = username;
        this.password = password;
        this.role = role;
        this.refId = refId;
    }
    public int getId(){return id;}
    public String getUsername(){return username;}
    public String getPassword(){return password;}
    public String getRole(){return role;}
    public Integer getRefId(){return refId;}
}

