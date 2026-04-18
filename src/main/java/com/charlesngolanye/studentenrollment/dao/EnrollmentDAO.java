package com.charlesngolanye.studentenrollment.dao;

import com.charlesngolanye.studentenrollment.model.Enrollment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EnrollmentDAO {

    private final Connection connection;

    public EnrollmentDAO(Connection connection) {
        this.connection = connection;
    }

    public void createTable() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS enrollments (
                       student_id INT,
                       course_id INT,
                       grade     DOUBLE,
                       PRIMARY KEY (student_id, course_id),
                       FOREIGN KEY (student_id) REFERENCES students(id),
                       FOREIGN KEY (course_id) REFERENCES courses(id)
                    )
            """);

        }
    }

    /*
     * reject if the course is at capacity or the student is already enrolled - goes to EnrollmentService
     */
    public void save(Enrollment enrollment) throws SQLException{
        String sql = "INSERT INTO enrollments (student_id, course_id, grade) VALUES (?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, enrollment.getStudentId());
            preparedStatement.setInt(2, enrollment.getCourseId());
            preparedStatement.setDouble(3, enrollment.getGrade()); // set to 0.0 initially? nullable
            preparedStatement.executeUpdate();
        }
    }

    /*
     * assign a numeric grade 0-100 - goes to EnrollmentService
     */
    public int update(Enrollment enrollment) throws SQLException {
        String sql = "UPDATE enrollments SET grade = ? WHERE student_id = ? AND course_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            if (enrollment.getGrade() != null) {
                preparedStatement.setDouble(1, enrollment.getGrade());
            } else {
                preparedStatement.setNull(1, Types.DOUBLE);
            }
            preparedStatement.setInt(2, enrollment.getStudentId());
            preparedStatement.setInt(3, enrollment.getCourseId());
            return preparedStatement.executeUpdate();
        }
    }


    public Optional<Enrollment> findEnrollmentByStudentId(int studentId) throws SQLException {
        String sql = "SELECT * FROM enrollments WHERE student_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, studentId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) return Optional.of(mapRow(resultSet));
            }
        }
        return Optional.empty();
    }

    public Optional<Enrollment> findEnrollmentByCourseId(int courseId) throws SQLException {
        String sql = "SELECT * FROM enrollments WHERE course_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, courseId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) return Optional.of(mapRow(resultSet));
            }
        }
        return Optional.empty();
    }

    public List<Enrollment> findAllEnrollments() throws SQLException {
        List<Enrollment> list = new ArrayList<>();
        String sql = "SELECT * FROM enrollments ORDER BY course_id";
        try(Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) list.add(mapRow(resultSet));
        }
        return list;
    }

    public boolean exists (int studentId, int courseId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM enrollments WHERE student_id = ? AND course_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, studentId);
            preparedStatement.setInt(2, courseId);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }

        }
        return false;
    }


    private Enrollment mapRow(ResultSet resultSet) throws SQLException {

        Double grade = resultSet.getObject("grade") != null ? resultSet.getDouble("grade") : null;

        return new Enrollment(
                resultSet.getInt("student_id"),
                resultSet.getInt("course_id"),
                grade
        );
    }

    public int countByCourse(int courseId) throws SQLException {
        String sql = "SELECT  COUNT(*) FROM enrollments WHERE course_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, courseId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) return resultSet.getInt(1);
        }
        return 0;
    }
}


