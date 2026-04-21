package com.charlesngolanye.studentenrollment.ui;

import com.charlesngolanye.studentenrollment.config.DatabaseConfig;
import com.charlesngolanye.studentenrollment.dao.CourseDAO;
import com.charlesngolanye.studentenrollment.dao.EnrollmentDAO;
import com.charlesngolanye.studentenrollment.dao.StudentDAO;
import com.charlesngolanye.studentenrollment.model.Course;
import com.charlesngolanye.studentenrollment.model.Enrollment;
import com.charlesngolanye.studentenrollment.model.Student;
import com.charlesngolanye.studentenrollment.service.CourseService;
import com.charlesngolanye.studentenrollment.service.EnrollmentService;
import com.charlesngolanye.studentenrollment.service.StudentService;
import com.charlesngolanye.studentenrollment.util.DatabaseInitializer;

import java.sql.Connection;
import java.util.ArrayList;
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
            CourseService courseService = new CourseService(courseDAO);

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

                    case 4:
                        System.out.println("Enter course code");
                        String courseCode = scanner.nextLine();

                        System.out.println("Enter course title");
                        String courseTitle = scanner.nextLine();

                        System.out.println("Enter course capacity");
                        int courseCapacity = scanner.nextInt();
                        scanner.nextLine();

                        Course course = new Course(courseCode, courseTitle, courseCapacity);
                        courseService.addCourse(course);

                        break;

                    case 5:
                        System.out.println("Enter Student Id");
                        int studentIde = scanner.nextInt();
                        scanner.nextLine(); // if an int and then you are taking another input...you consume the whole line
                        List<Enrollment> enrollments = enrollmentService.enrollmentByStudentId(studentIde);
//                        List<Course> courses = new ArrayList<>();
//                        List<Student> students = new ArrayList<>();
//                        for (Enrollment en : enrollments){
//                            //get the course -> in every enrollment there is a courseId
//                            Course courseE = courseDAO.findByCourseId(en.getCourseId()).orElse(new Course());
//                            courses.add(courseE);
//                            Student student = studentDAO.findById(en.getStudentId()).orElse(new Student());
//                            students.add(student);
//                        }
                        System.out.println(enrollments);
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
                    4. Add Course
                    5. List Enrollments by Student Id
                    0. Exit
        """);

    }


/**
 * remove throws exception in DAOs replay w try / catch - done
 *  clean services -> remove method level throws exceptions - done
 *  add new methods course report, performers etc
 *
 */



}
