package com.charlesngolanye.studentenrollment.service;

import com.charlesngolanye.studentenrollment.dao.StudentDAO;
import com.charlesngolanye.studentenrollment.model.Student;


import java.util.List;

public class StudentService {

    private final StudentDAO studentDAO;

    public StudentService(StudentDAO studentDAO) {
        this.studentDAO = studentDAO;
    }

    public void addNewStudent(Student student){
        studentDAO.save(student);
    }

    public List<Student> studentList() {
        return studentDAO.listAll();
    }

    public void deleteStudent (int id){
        studentDAO.delete(id);
    }
}

