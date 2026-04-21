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


    // Enroll student
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



    // Assign grade
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


    public List<Enrollment> findEnrollmentByStudentId(int studentId)  {
        List<Enrollment> list = new ArrayList<>();

        String sql = "SELECT * FROM enrollments WHERE student_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, studentId);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()){
                Enrollment enrollment = new Enrollment(studentId,rs.getInt("course_id"), rs.getDouble("grade"));
                list.add(enrollment);
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
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                Enrollment enrollment = new Enrollment(resultSet.getInt("student_id"), courseId, resultSet.getDouble("grade"));
                enrollments.add(enrollment);
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

    public boolean exists (int studentId, int courseId){
        String sql = "SELECT COUNT(*) FROM enrollments WHERE student_id = ? AND course_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, studentId);
            preparedStatement.setInt(2, courseId);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
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

    public int countByCourse(int courseId){
        String sql = "SELECT  COUNT(*) FROM enrollments WHERE course_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, courseId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) return resultSet.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}


