package com.charlesngolanye.studentenrollment.dao;

import com.charlesngolanye.studentenrollment.model.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StudentDAO {
    private final Connection connection;

    public StudentDAO(Connection connection) {
        this.connection = connection;
    }

    public void createTable(){
        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS students (
                       id INT PRIMARY KEY AUTO_INCREMENT,
                       name VARCHAR(100) NOT NULL,
                       email VARCHAR(150) UNIQUE NOT NULL
                    )
            """);

        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void save(Student student){
        String sql = "INSERT INTO students (name, email) VALUES (?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, student.getName());
            preparedStatement.setString(2, student.getEmail());
            preparedStatement.executeUpdate();

        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public Optional<Student> findById(int id){
        String sql = "SELECT * FROM students WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) return Optional.of(mapRow(resultSet));
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public List<Student> listAll(){
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM students ORDER BY name";
        try(Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) list.add(mapRow(resultSet));

        }catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /*
    * before deleting -> must check enrollments (via service or DAO call)..yet to implement - goes to EnrollmentService
     */
    public int delete(int id){
        String sql = "DELETE FROM students WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        }
        return -1;
    }

    private Student mapRow(ResultSet resultSet){
        try {
            return new Student(
                    resultSet.getInt("id"),
                    resultSet.getString("name"),
                    resultSet.getString("email")
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // not sure abt the null
    }
}
