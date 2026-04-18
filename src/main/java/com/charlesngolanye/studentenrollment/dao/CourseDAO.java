package com.charlesngolanye.studentenrollment.dao;

import com.charlesngolanye.studentenrollment.model.Course;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CourseDAO {

    private final Connection connection;

    public CourseDAO(Connection connection) {
        this.connection = connection;
    }

    public void createTable() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS courses (
                       id INT PRIMARY KEY AUTO_INCREMENT,
                       code VARCHAR(20)  UNIQUE NOT NULL,
                       title VARCHAR(150) NOT NULL,
                       capacity INT NOT NULL
                    )
            """);

        }
    }

    public void save(Course course) throws SQLException{
        String sql = "INSERT INTO courses (code, title, capacity) VALUES (?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, course.getCode());
            preparedStatement.setString(2, course.getTitle());
            preparedStatement.setInt(3, course.getCapacity());
            preparedStatement.executeUpdate();
        }
    }

    public Optional<Course> findById(int id) throws SQLException {
        String sql = "SELECT * FROM courses WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) return Optional.of(mapRow(resultSet));
            }
        }
        return Optional.empty();
    }

    public List<Course> listAll() throws SQLException {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT * FROM courses ORDER BY title";
        try(Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) list.add(mapRow(resultSet));
        }
        return list;
    }

    /*
     * before deleting -> must check enrollments - cannot delete if enrollment exists (via service or DAO call)..yet to implement - goes to EnrollmentService
     */
    public int delete(int id) throws SQLException {
        String sql = "DELETE FROM courses WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate(); // executeUpdate always returns no. of rows affected which is always an int
        }
    }

    private Course mapRow(ResultSet resultSet) throws SQLException {
        return new Course(
                resultSet.getInt("id"),
                resultSet.getString("code"),
                resultSet.getString("title"),
                resultSet.getInt("capacity")
        );
    }
}


