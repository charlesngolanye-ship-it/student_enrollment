package com.charlesngolanye.studentenrollment.model;

public class Student {
    private int id;
    private String name;
    private String email;

    public Student(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public Student(String name, String email){
        this.name= name;
        this.email = email;
    }

    public Student(){}
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Student: " + id + " | " + name + " | " + email;

    }
}
/**
 * Treated id as final -> should not be set afterwards
 */