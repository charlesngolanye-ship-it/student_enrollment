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

    public void createTable(){
        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS courses (
                       id INT PRIMARY KEY AUTO_INCREMENT,
                       code VARCHAR(20)  UNIQUE NOT NULL,
                       title VARCHAR(150) NOT NULL,
                       capacity INT NOT NULL
                    )
            """);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void save(Course course){
        String sql = "INSERT INTO courses (code, title, capacity) VALUES (?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, course.getCode());
            preparedStatement.setString(2, course.getTitle());
            preparedStatement.setInt(3, course.getCapacity());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Optional<Course> findByCourseId(int id){
        String sql = "SELECT * FROM courses WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) return Optional.of(mapRow(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<Course> findCourseByCode(String code){
        // BUG FIX: column is named "code", not "course_code" — always returned empty before
        String sql = "SELECT * FROM courses WHERE code = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, code);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) return Optional.of(mapRow(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public List<Course> listAll(){
        List<Course> list = new ArrayList<>();
        String sql = "SELECT * FROM courses ORDER BY title";

        try(Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) list.add(mapRow(resultSet));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }


    public int delete(int id){
        String sql = "DELETE FROM courses WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate(); // executeUpdate always returns no. of rows affected which is always an int

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private Course mapRow(ResultSet resultSet){
        try {
            return new Course(
                    resultSet.getInt("id"),
                    resultSet.getString("code"),
                    resultSet.getString("title"),
                    resultSet.getInt("capacity")
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // not sure abt the null
    }
}


