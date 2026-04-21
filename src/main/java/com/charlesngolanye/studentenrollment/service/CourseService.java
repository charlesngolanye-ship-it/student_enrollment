package com.charlesngolanye.studentenrollment.service;

import com.charlesngolanye.studentenrollment.dao.CourseDAO;
import com.charlesngolanye.studentenrollment.model.Course;
import com.charlesngolanye.studentenrollment.model.Enrollment;


import java.util.ArrayList;
import java.util.List;

public class CourseService {
    private final CourseDAO courseDAO;

    public CourseService(CourseDAO courseDAO) {
        this.courseDAO = courseDAO;
    }

    public void addCourse(Course course) {
            courseDAO.save( course);
    }

    public List<Course> courseList (Course course) {
        List<Course> courseList = new ArrayList<>();
            courseList = courseDAO.listAll();

        return courseList;
    }

    public int findCourseById(int id) {
            courseDAO.findByCourseId(id);

        return id;
    }

    public void deleteCourse (int id, Enrollment enrollment)  {
        int courseIdToDelete = findCourseById(id);

            // Cannot delete a course with active enrollments
        if (courseIdToDelete != enrollment.getCourseId()) {
                courseDAO.delete(id);
        }
    }
}
