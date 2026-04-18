package com.charlesngolanye.studentenrollment.service;

import com.charlesngolanye.studentenrollment.dao.CourseDAO;
import com.charlesngolanye.studentenrollment.dao.EnrollmentDAO;
import com.charlesngolanye.studentenrollment.dao.StudentDAO;
import com.charlesngolanye.studentenrollment.model.Course;
import com.charlesngolanye.studentenrollment.model.Enrollment;
import com.charlesngolanye.studentenrollment.model.Student;

import java.util.Optional;


public class EnrollmentService {
    private final StudentDAO studentDAO;
    private final CourseDAO courseDAO;
    private final EnrollmentDAO enrollmentDAO;

    public EnrollmentService(StudentDAO studentDAO, CourseDAO courseDAO, EnrollmentDAO enrollmentDAO) {
        this.studentDAO = studentDAO;
        this.courseDAO = courseDAO;
        this.enrollmentDAO = enrollmentDAO;
    }


    public void enrollStudent(int studentId, int id) throws Exception {

        Optional<Student> student = studentDAO.findById(studentId);
        if (student.isEmpty()) {
            throw new Exception("Student not found");
        }

        Optional<Course> courseOptional = courseDAO.findById(id);
        if (courseOptional.isEmpty()) {
            throw  new Exception("Course not found");
        }
        Course course = courseOptional.get();

        boolean exists = enrollmentDAO.exists(studentId, course.getId());
        if (exists) {
            throw new Exception("Student is already enrolled in this course");
        }

        int currentCount = enrollmentDAO.countByCourse(course.getId());
        if (currentCount >= course.getCapacity()) {
            throw new Exception("The course is full");
        }

        Enrollment enrollment = new Enrollment(studentId, course.getId(), null);
        enrollmentDAO.save(enrollment);
    }



}
/*
 * add dependencies private and final
 * all dependencies injected using the constructor ...you create field, then a constructor
 * DAO methods use 'this.connection'
 * The StudentService is the boss. It imports the DatabaseConfig to grab a connection, then 'injects' that connection into the DAO's constructor
 */
