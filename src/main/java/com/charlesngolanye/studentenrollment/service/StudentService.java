package com.charlesngolanye.studentenrollment.service;

import com.charlesngolanye.studentenrollment.dao.StudentDAO;
import com.charlesngolanye.studentenrollment.model.Student;

import java.util.List;
import java.util.Optional;

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

    public Optional<Student> findStudentById(int id) {
        return studentDAO.findById(id);
    }

    // NOTE: enrollment check before delete is in EnrollmentService (cross-DAO concern)
    public int deleteStudent(int id){
        return studentDAO.delete(id);
    }
}
