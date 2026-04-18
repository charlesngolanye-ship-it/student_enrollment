package com.charlesngolanye.studentenrollment.service;

import com.charlesngolanye.studentenrollment.dao.CourseDAO;
import com.charlesngolanye.studentenrollment.model.Course;
import com.charlesngolanye.studentenrollment.model.Enrollment;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CourseService {
    private final CourseDAO courseDAO;

    public CourseService(CourseDAO courseDAO) {
        this.courseDAO = courseDAO;
    }

    public void addCourse(Course course) {
        try {
            courseDAO.save( course);
        } catch (SQLException e) {
            e.printStackTrace(); // should prob not be a runtime exception
        }
    }

    public List<Course> courseList (Course course) {
        List<Course> courseList = new ArrayList<>();

        try{
            courseList = courseDAO.listAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courseList;
    }

    public int findCourseById(int id) {
        try {
            courseDAO.findById(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    public void deleteCourse (int id, Enrollment enrollment)  {
        int courseIdToDelete = findCourseById(id);

        try {
            // Cannot delete a course with active enrollments
            if (courseIdToDelete != enrollment.getCourseId()) {
                courseDAO.delete(id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
