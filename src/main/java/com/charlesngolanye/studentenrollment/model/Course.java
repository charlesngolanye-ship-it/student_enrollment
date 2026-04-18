package com.charlesngolanye.studentenrollment.model;

public class Course {
    private int id;
    private final String code;
    private String title;
    private int capacity;

    public Course(int id, String code, String title, int capacity) {
        this.id = id;
        this.code = code;
        this.title = title;
        this.capacity = capacity;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public String toString() {
        return "Course: " + code + " | " + title + " | cap=" + capacity;
    }
}

/**
 * Treated Code as final -> not sure if id should be the final one - immutability/set once
 */
