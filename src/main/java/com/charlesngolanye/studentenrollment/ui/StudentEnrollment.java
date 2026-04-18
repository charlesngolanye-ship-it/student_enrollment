package com.charlesngolanye.studentenrollment.ui;

import com.charlesngolanye.studentenrollment.config.DatabaseConfig;
import com.charlesngolanye.studentenrollment.dao.CourseDAO;
import com.charlesngolanye.studentenrollment.dao.EnrollmentDAO;
import com.charlesngolanye.studentenrollment.dao.StudentDAO;
import com.charlesngolanye.studentenrollment.model.Student;
import com.charlesngolanye.studentenrollment.service.EnrollmentService;
import com.charlesngolanye.studentenrollment.service.StudentService;
import com.charlesngolanye.studentenrollment.util.DatabaseInitializer;

import java.sql.Connection;
import java.util.List;
import java.util.Scanner;

public class StudentEnrollment {
    public static void main(String[] args) throws Exception {

        try (Connection connection = DatabaseConfig.getConnection()) {

            StudentDAO studentDAO = new StudentDAO(connection);
            CourseDAO courseDAO = new CourseDAO(connection);
            EnrollmentDAO enrollmentDAO = new EnrollmentDAO(connection);

            DatabaseInitializer.databaseInitializer(studentDAO, courseDAO, enrollmentDAO);

            StudentService studentService = new StudentService(studentDAO);
            EnrollmentService enrollmentService = new EnrollmentService(studentDAO, courseDAO, enrollmentDAO);

            Scanner scanner = new Scanner(System.in);

            while (true) {
                printMenu();

                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        System.out.println("Name: ");
                        String name = scanner.nextLine();

                        System.out.println("Email: ");
                        String email = scanner.nextLine();

                        studentService.addNewStudent(new Student(name, email));
                        break;

                    case 2:
                        List<Student> studentList = studentService.studentList();
                        for (Student student: studentList)
                            System.out.println(student);

                        break;

                    case 3:
                        System.out.println("Student ID: ");
                        int studentId = scanner.nextInt();
                        scanner.nextLine();

                        System.out.println("Course id: ");
                        int courseId = scanner.nextInt();
                        scanner.nextLine();

                        enrollmentService.enrollStudent(studentId, courseId);
                        break;

                    case 0:
                        return;

                }
            }

        }

    }


    private static void printMenu(){
        System.out.println("""
                    1. Add Student
                    2. List Students
                    3. Enroll Student
                    0. Exit
        """);

    }







}
