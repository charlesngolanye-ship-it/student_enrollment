package com.charlesngolanye.studentenrollment.model;

public class Enrollment {
    private int studentId;
    private int courseId;
    private Double grade; // change from double to Double to make it nullable?

    public Enrollment(int studentId, int courseId, Double grade) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.grade = grade;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public Double getGrade() {
        return grade;
    }

    public void setGrade(Double grade) {
        this.grade = grade;
    }

    @Override
    public String toString() {
        return "Enrollment: student=" + studentId + " course= " + courseId + " grade=" + grade;
    }
}

/**
 * Student class has id -> but here I have studentID -> what is the r/ship between Student and Enrollment? same with Course
 * Did I really need Enrollment class? join table
 */