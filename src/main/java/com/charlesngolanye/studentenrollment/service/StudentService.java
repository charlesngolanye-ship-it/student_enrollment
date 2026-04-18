package com.charlesngolanye.studentenrollment.service;

import com.charlesngolanye.studentenrollment.dao.StudentDAO;
import com.charlesngolanye.studentenrollment.model.Student;

import java.sql.SQLException;
import java.util.List;

public class StudentService {

    private final StudentDAO studentDAO;

    public StudentService(StudentDAO studentDAO) {
        this.studentDAO = studentDAO;
    }

    public void addNewStudent(Student student) throws SQLException {
        studentDAO.save(student);

    }

    public List<Student> studentList() throws SQLException{
        return studentDAO.listAll();

    }

//    public int findStudentById (int id) {
//        try (Connection conn = DatabaseConfig.getConnection()) {
//            StudentDAO dao = new StudentDAO(conn);
//            dao.findById(id);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return id;
//    }

    public void deleteStudent (int id) throws SQLException {
        studentDAO.delete(id);

    }
}
/*
* Find out if I could use throws instead of try catch
 */
