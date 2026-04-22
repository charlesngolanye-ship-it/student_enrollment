package com.charlesngolanye.studentenrollment.service;

import com.charlesngolanye.studentenrollment.dao.CourseDAO;
import com.charlesngolanye.studentenrollment.model.Course;

import java.util.List;
import java.util.Optional;

public class CourseService {
    private final CourseDAO courseDAO;

    public CourseService(CourseDAO courseDAO) {
        this.courseDAO = courseDAO;
    }

    public void addCourse(Course course) {
        courseDAO.save(course);
    }

    // BUG FIX: old method had an unused Course parameter and a redundant local variable
    public List<Course> courseList() {
        return courseDAO.listAll();
    }

    // BUG FIX: old method ignored the DAO result and returned the raw int id
    public Optional<Course> findCourseById(int id) {
        return courseDAO.findByCourseId(id);
    }

    public Optional<Course> findCourseByCode(String code) {
        return courseDAO.findCourseByCode(code);
    }

    // NOTE: enrollment check before delete is in EnrollmentService (cross-DAO concern)
    public int deleteCourse(int id) {
        return courseDAO.delete(id);
    }
}
