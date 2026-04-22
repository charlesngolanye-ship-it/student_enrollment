package com.charlesngolanye.studentenrollment.ui;

import com.charlesngolanye.studentenrollment.config.DatabaseConfig;
import com.charlesngolanye.studentenrollment.dao.CourseDAO;
import com.charlesngolanye.studentenrollment.dao.EnrollmentDAO;
import com.charlesngolanye.studentenrollment.dao.StudentDAO;
import com.charlesngolanye.studentenrollment.model.Course;
import com.charlesngolanye.studentenrollment.model.Student;
import com.charlesngolanye.studentenrollment.service.CourseService;
import com.charlesngolanye.studentenrollment.service.EnrollmentService;
import com.charlesngolanye.studentenrollment.service.StudentService;
import com.charlesngolanye.studentenrollment.util.DatabaseInitializer;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class StudentEnrollment {
    public static void main(String[] args) throws Exception {

        try (Connection connection = DatabaseConfig.getConnection()) {

            StudentDAO studentDAO = new StudentDAO(connection);
            CourseDAO courseDAO = new CourseDAO(connection);
            EnrollmentDAO enrollmentDAO = new EnrollmentDAO(connection);

            DatabaseInitializer.databaseInitializer(studentDAO, courseDAO, enrollmentDAO);

            StudentService studentService = new StudentService(studentDAO);
            CourseService courseService = new CourseService(courseDAO);
            // EnrollmentService receives the Connection so it can manage transactions
            EnrollmentService enrollmentService = new EnrollmentService(connection, studentDAO, courseDAO, enrollmentDAO);

            Scanner scanner = new Scanner(System.in);

            while (true) {
                printMenu();

                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {

                    // ── Students ──────────────────────────────────────────────
                    case 1: {
                        System.out.print("Name: ");
                        String name = scanner.nextLine();
                        System.out.print("Email: ");
                        String email = scanner.nextLine();
                        studentService.addNewStudent(new Student(name, email));
                        System.out.println("Student added.");
                        break;
                    }
                    case 2: {
                        List<Student> students = studentService.studentList();
                        if (students.isEmpty()) System.out.println("No students registered.");
                        else students.forEach(System.out::println);
                        break;
                    }
                    case 3: {
                        System.out.print("Student ID: ");
                        int id = scanner.nextInt();
                        scanner.nextLine();
                        Optional<Student> student = studentService.findStudentById(id);
                        student.ifPresentOrElse(System.out::println,
                                () -> System.out.println("Student not found with id: " + id));
                        break;
                    }
                    case 4: {
                        System.out.print("Student ID to delete: ");
                        int id = scanner.nextInt();
                        scanner.nextLine();
                        enrollmentService.deleteStudent(id);
                        break;
                    }

                    // ── Courses ───────────────────────────────────────────────
                    case 5: {
                        System.out.print("Course code: ");
                        String code = scanner.nextLine();
                        System.out.print("Course title: ");
                        String title = scanner.nextLine();
                        System.out.print("Capacity: ");
                        int capacity = scanner.nextInt();
                        scanner.nextLine();
                        courseService.addCourse(new Course(code, title, capacity));
                        System.out.println("Course added.");
                        break;
                    }
                    case 6: {
                        List<Course> courses = courseService.courseList();
                        if (courses.isEmpty()) System.out.println("No courses available.");
                        else courses.forEach(System.out::println);
                        break;
                    }
                    case 7: {
                        System.out.print("Course code: ");
                        String code = scanner.nextLine();
                        Optional<Course> course = courseService.findCourseByCode(code);
                        course.ifPresentOrElse(System.out::println,
                                () -> System.out.println("Course not found with code: " + code));
                        break;
                    }
                    case 8: {
                        System.out.print("Course ID to delete: ");
                        int id = scanner.nextInt();
                        scanner.nextLine();
                        enrollmentService.deleteCourse(id);
                        break;
                    }

                    // ── Enrollments ───────────────────────────────────────────
                    case 9: {
                        System.out.print("Student ID: ");
                        int studentId = scanner.nextInt();
                        System.out.print("Course ID: ");
                        int courseId = scanner.nextInt();
                        scanner.nextLine();
                        enrollmentService.enrollStudent(studentId, courseId);
                        break;
                    }
                    case 10: {
                        System.out.print("Student ID: ");
                        int studentId = scanner.nextInt();
                        System.out.print("Course ID: ");
                        int courseId = scanner.nextInt();
                        System.out.print("Grade (0-100): ");
                        double grade = scanner.nextDouble();
                        scanner.nextLine();
                        enrollmentService.assignGrade(grade, studentId, courseId);
                        break;
                    }

                    // ── Reports ───────────────────────────────────────────────
                    case 11: {
                        System.out.print("Student ID: ");
                        int studentId = scanner.nextInt();
                        scanner.nextLine();
                        enrollmentService.studentReport(studentId);
                        break;
                    }
                    case 12: {
                        System.out.print("Course ID: ");
                        int courseId = scanner.nextInt();
                        scanner.nextLine();
                        enrollmentService.courseReport(courseId);
                        break;
                    }
                    case 13: {
                        System.out.print("Minimum GPA threshold: ");
                        double threshold = scanner.nextDouble();
                        scanner.nextLine();
                        System.out.println("\n=== Top Performers (GPA >= " + threshold + ") ===");
                        List<Student> top = enrollmentService.topPerformers(threshold);
                        if (top.isEmpty()) System.out.println("  No students meet the threshold.");
                        top.forEach(System.out::println);
                        break;
                    }
                    case 14: {
                        System.out.print("Course ID: ");
                        int courseId = scanner.nextInt();
                        scanner.nextLine();
                        System.out.print("Output file path (e.g. report.csv): ");
                        String path = scanner.nextLine();
                        enrollmentService.exportCourseReportToCsv(courseId, path);
                        break;
                    }

                    case 0:
                        System.out.println("Goodbye!");
                        return;

                    default:
                        System.out.println("Invalid option, please try again.");
                }
            }
        }
    }

    private static void printMenu() {
        System.out.println("""
                \n=== Student Enrollment System ===
                -- Students --
                 1.  Add Student
                 2.  List Students
                 3.  Find Student by ID
                 4.  Delete Student
                -- Courses --
                 5.  Add Course
                 6.  List Courses
                 7.  Find Course by Code
                 8.  Delete Course
                -- Enrollments --
                 9.  Enroll Student in Course
                 10. Assign Grade
                -- Reports --
                 11. Student Report
                 12. Course Report
                 13. Top Performers
                 14. Export Course Report to CSV
                 0.  Exit
                """);
        System.out.print("Choice: ");
    }
}
