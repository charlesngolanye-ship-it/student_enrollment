package com.charlesngolanye.studentenrollment.dao;

import com.charlesngolanye.studentenrollment.model.Enrollment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentDAO {

    private final Connection connection;

    public EnrollmentDAO(Connection connection) {
        this.connection = connection;
    }

    public void createTable(){
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

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insert(Enrollment enrollment){
        String sql = "INSERT INTO enrollments (student_id, course_id) VALUES (?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, enrollment.getStudentId());
            preparedStatement.setInt(2, enrollment.getCourseId());
            preparedStatement.executeUpdate();

        } catch (SQLException e){
            System.err.println(e.getMessage());
        }
    }

    public int assignGrade(int studentId, int courseId, Double grade){
        String sql = "UPDATE enrollments SET grade = ? WHERE student_id = ? AND course_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setDouble(1, grade);
            preparedStatement.setInt(2, studentId);
            preparedStatement.setInt(3, courseId);
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public List<Enrollment> findEnrollmentByStudentId(int studentId) {
        List<Enrollment> list = new ArrayList<>();
        String sql = "SELECT * FROM enrollments WHERE student_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);) {
            preparedStatement.setInt(1, studentId);
            // BUG FIX: ResultSet was not closed — now wrapped in try-with-resources
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Enrollment> findEnrollmentByCourseId(int courseId){
        List<Enrollment> enrollments = new ArrayList<>();
        String sql = "SELECT * FROM enrollments WHERE course_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, courseId);
            // BUG FIX: ResultSet was not closed — now wrapped in try-with-resources
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    enrollments.add(mapRow(resultSet));
                }
            }
        } catch (SQLException e){
            System.err.println(e.getMessage());
        }
        return enrollments;
    }

    public List<Enrollment> listEnrollments(){
        List<Enrollment> list = new ArrayList<>();
        String sql = "SELECT * FROM enrollments ORDER BY course_id";
        try(Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) list.add(mapRow(resultSet));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean exists(int studentId, int courseId){
        String sql = "SELECT COUNT(*) FROM enrollments WHERE student_id = ? AND course_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, studentId);
            preparedStatement.setInt(2, courseId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) return resultSet.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public int countByCourse(int courseId){
        String sql = "SELECT COUNT(*) FROM enrollments WHERE course_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, courseId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int countByStudent(int studentId){
        String sql = "SELECT COUNT(*) FROM enrollments WHERE student_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, studentId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Returns the average grade for a student across all graded courses (used for GPA / top performers)
    public Double getAverageGrade(int studentId){
        String sql = "SELECT AVG(grade) FROM enrollments WHERE student_id = ? AND grade IS NOT NULL";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, studentId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    double avg = resultSet.getDouble(1);
                    return resultSet.wasNull() ? null : avg;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Enrollment mapRow(ResultSet resultSet){
        try {
            Double grade = resultSet.getObject("grade") != null ? resultSet.getDouble("grade") : null;
            return new Enrollment(
                    resultSet.getInt("student_id"),
                    resultSet.getInt("course_id"),
                    grade
            );
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }
}
