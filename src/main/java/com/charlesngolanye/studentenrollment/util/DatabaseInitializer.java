package com.charlesngolanye.studentenrollment.util;

import com.charlesngolanye.studentenrollment.dao.CourseDAO;
import com.charlesngolanye.studentenrollment.dao.EnrollmentDAO;
import com.charlesngolanye.studentenrollment.dao.StudentDAO;

import java.sql.SQLException;

public class DatabaseInitializer {

    public static void databaseInitializer(StudentDAO studentDAO, CourseDAO courseDAO, EnrollmentDAO enrollmentDAO) throws SQLException {
        studentDAO.createTable();
        courseDAO.createTable();
        enrollmentDAO.createTable();
    }
}
