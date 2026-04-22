package com.charlesngolanye.studentenrollment.util;

import com.charlesngolanye.studentenrollment.dao.CourseDAO;
import com.charlesngolanye.studentenrollment.dao.EnrollmentDAO;
import com.charlesngolanye.studentenrollment.dao.StudentDAO;

public class DatabaseInitializer {

    // BUG FIX: was declared "throws SQLException" but all three DAOs catch their own SQLExceptions internally
    public static void databaseInitializer(StudentDAO studentDAO, CourseDAO courseDAO, EnrollmentDAO enrollmentDAO) {
        studentDAO.createTable();
        courseDAO.createTable();
        enrollmentDAO.createTable();
    }
}
