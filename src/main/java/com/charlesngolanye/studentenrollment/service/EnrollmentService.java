package com.charlesngolanye.studentenrollment.service;

import com.charlesngolanye.studentenrollment.dao.CourseDAO;
import com.charlesngolanye.studentenrollment.dao.EnrollmentDAO;
import com.charlesngolanye.studentenrollment.dao.StudentDAO;
import com.charlesngolanye.studentenrollment.model.Course;
import com.charlesngolanye.studentenrollment.model.Enrollment;
import com.charlesngolanye.studentenrollment.model.Student;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EnrollmentService {
    private static final double MIN_GRADE = 0.0;
    private static final double MAX_GRADE = 100.0;

    private final Connection connection;
    private final StudentDAO studentDAO;
    private final CourseDAO courseDAO;
    private final EnrollmentDAO enrollmentDAO;

    public EnrollmentService(Connection connection, StudentDAO studentDAO, CourseDAO courseDAO, EnrollmentDAO enrollmentDAO) {
        this.connection = connection;
        this.studentDAO = studentDAO;
        this.courseDAO = courseDAO;
        this.enrollmentDAO = enrollmentDAO;
    }

    /**
     * Enroll a student in a course.
     * Rejects if the student or course does not exist, the student is already enrolled,
     * or the course is at capacity.
     * BONUS: capacity check + insert are wrapped in a transaction to prevent race conditions.
     */
    public void enrollStudent(int studentId, int courseId) {
        // check if student exists
        Optional<Student> student = studentDAO.findById(studentId);
        if (student.isEmpty()) {
            System.out.println("Student not found with id: " + studentId);
            return;
        }

        // check if course exists
        // BUG FIX: missing return after this check — enrollment continued even when course was missing
        Optional<Course> courseOptional = courseDAO.findByCourseId(courseId);
        if (courseOptional.isEmpty()) {
            System.out.println("Course not found with id: " + courseId);
            return;
        }
        Course course = courseOptional.get();

        // Wrap the capacity check + insert in a transaction to prevent a race condition
        // where two requests both pass the capacity check and both insert, exceeding capacity.
        boolean prevAutoCommit = true;
        try {
            prevAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            // BUG FIX: missing return after this check — enrollment continued even when student was already enrolled
            if (enrollmentDAO.exists(studentId, courseId)) {
                System.out.println("Student is already enrolled in this course");
                connection.rollback();
                return;
            }

            int currentCount = enrollmentDAO.countByCourse(courseId);
            if (currentCount >= course.getCapacity()) {
                System.out.println("Course is at full capacity (" + course.getCapacity() + " students)");
                connection.rollback();
                return;
            }

            enrollmentDAO.insert(new Enrollment(studentId, courseId));
            connection.commit();
            System.out.println("Enrolled " + student.get().getName() + " in " + course.getTitle());

        } catch (SQLException e) {
            try { 
                connection.rollback(); 
            } catch (SQLException ex) { 
                ex.printStackTrace(); }
            e.printStackTrace();
        } finally {
            try { connection.setAutoCommit(prevAutoCommit); 

            } catch (SQLException e) {
                 e.printStackTrace(); 
                }
        }
    }

    /**
     * Assign a numeric grade (0–100) to an existing enrollment.
     */
    public void assignGrade(double grade, int studentId, int courseId) {
        // BUG FIX: was "grade > MIN_GRADE" which excluded the valid grade of 0.0
        if (grade >= MIN_GRADE && grade <= MAX_GRADE) {
            if (enrollmentDAO.exists(studentId, courseId)) {
                enrollmentDAO.assignGrade(studentId, courseId, grade);
                System.out.println("Grade " + grade + " assigned to student " + studentId + " for course " + courseId);
            } else {
                System.out.println("No enrollment found for student " + studentId + " in course " + courseId);
            }
        } else {
            System.out.println("Grade must be between 0 and 100");
        }
    }

    /**
     * Student report — list all courses a student is enrolled in with their grade.
     */
    public void studentReport(int studentId) {
        Optional<Student> studentOpt = studentDAO.findById(studentId);
        if (studentOpt.isEmpty()) {
            System.out.println("Student not found with id: " + studentId);
            return;
        }
        Student student = studentOpt.get();
        List<Enrollment> enrollments = enrollmentDAO.findEnrollmentByStudentId(studentId);

        System.out.println("\n=== Student Report: " + student.getName() + " ===");
        if (enrollments.isEmpty()) {
            System.out.println("  Not enrolled in any courses.");
            return;
        }
        for (Enrollment e : enrollments) {
            Optional<Course> courseOpt = courseDAO.findByCourseId(e.getCourseId());
            String courseName = courseOpt.map(c -> c.getCode() + " - " + c.getTitle()).orElse("Unknown course");
            String grade = e.getGrade() != null ? String.format("%.1f", e.getGrade()) : "No grade";
            System.out.println("  " + courseName + "  |  Grade: " + grade);
        }
        System.out.println();
    }

    /**
     * Course report — list all enrolled students with grades and show the class average.
     * BUG FIX: old method had a compile error (incomplete statement) and no output
     */
    public void courseReport(int courseId) {
        Optional<Course> courseOpt = courseDAO.findByCourseId(courseId);
        if (courseOpt.isEmpty()) {
            System.out.println("Course not found with id: " + courseId);
            return;
        }
        Course course = courseOpt.get();
        List<Enrollment> enrollments = enrollmentDAO.findEnrollmentByCourseId(courseId);

        System.out.println("\n=== Course Report: " + course.getCode() + " - " + course.getTitle() + " ===");
        if (enrollments.isEmpty()) {
            System.out.println("  No students enrolled.");
            return;
        }

        double total = 0;
        int gradedCount = 0;
        for (Enrollment e : enrollments) {
            Optional<Student> studentOpt = studentDAO.findById(e.getStudentId());
            String name = studentOpt.map(Student::getName).orElse("Unknown");
            String grade = e.getGrade() != null ? String.format("%.1f", e.getGrade()) : "No grade";
            System.out.println("  " + name + "  |  Grade: " + grade);
            if (e.getGrade() != null) {
                total += e.getGrade();
                gradedCount++;
            }
        }

        if (gradedCount > 0) {
            double average = total / gradedCount;
            System.out.printf("  Class average: %.2f%n%n", average);
        } else {
            System.out.println("  No grades recorded yet.\n");
        }
    }

    /**
     * List all students whose GPA (average across all graded courses) is above the given threshold.
     * BUG FIX: old method was empty and had the wrong signature (took studentId + grade instead of a threshold)
     */
    public List<Student> topPerformers(double threshold) {
        List<Student> allStudents = studentDAO.listAll();
        List<Student> top = new ArrayList<>();
        for (Student student : allStudents) {
            Double gpa = enrollmentDAO.getAverageGrade(student.getId());
            if (gpa != null && gpa >= threshold) {
                top.add(student);
                System.out.printf("  %s  |  GPA: %.2f%n", student.getName(), gpa);
            }
        }
        return top;
    }

    /**
     * Delete a student only if they have no active enrollments.
     * BUG FIX: the old StudentService.deleteStudent() called the DAO directly without this check,
     * which would fail with a foreign-key constraint error at the DB level (cryptic message).
     * Now the check is here in EnrollmentService since it spans two DAOs.
     */
    public void deleteStudent(int id) {
        if (enrollmentDAO.countByStudent(id) > 0) {
            System.out.println("Cannot delete student " + id + ": they are currently enrolled in one or more courses");
            return;
        }
        int rows = studentDAO.delete(id);
        if (rows > 0) System.out.println("Student " + id + " deleted");
        else System.out.println("Student not found with id: " + id);
    }

    /**
     * Delete a course only if it has no active enrollments.
     * BUG FIX: the old CourseService.deleteCourse() took a single Enrollment object and compared IDs
     * incorrectly — it only deleted if the course ID did NOT match, which is backwards.
     * Now the check is here in EnrollmentService since it spans two DAOs.
     */
    public void deleteCourse(int id) {
        if (enrollmentDAO.countByCourse(id) > 0) {
            System.out.println("Cannot delete course " + id + ": there are active enrollments");
            return;
        }
        int rows = courseDAO.delete(id);
        if (rows > 0) System.out.println("Course " + id + " deleted");
        else System.out.println("Course not found with id: " + id);
    }

    /**
     * Return raw enrollments for a student (used for display in the UI).
     */
    public List<Enrollment> enrollmentByStudentId(int studentId){
        return enrollmentDAO.findEnrollmentByStudentId(studentId);
    }

    /**
     * BONUS: Export a course report to a CSV file.
     */
    public void exportCourseReportToCsv(int courseId, String filePath) {
        Optional<Course> courseOpt = courseDAO.findByCourseId(courseId);
        if (courseOpt.isEmpty()) {
            System.out.println("Course not found with id: " + courseId);
            return;
        }
        Course course = courseOpt.get();
        List<Enrollment> enrollments = enrollmentDAO.findEnrollmentByCourseId(courseId);

        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("# Course: " + course.getCode() + " - " + course.getTitle());
            writer.println("student_id,student_name,grade");

            double total = 0;
            int gradedCount = 0;
            for (Enrollment e : enrollments) {
                Optional<Student> studentOpt = studentDAO.findById(e.getStudentId());
                String name = studentOpt.map(Student::getName).orElse("Unknown");
                String grade = e.getGrade() != null ? String.valueOf(e.getGrade()) : "";
                writer.println(e.getStudentId() + "," + name + "," + grade);
                if (e.getGrade() != null) {
                    total += e.getGrade();
                    gradedCount++;
                }
            }
            if (gradedCount > 0) {
                writer.printf(",,Average: %.2f%n", total / gradedCount);
            }
            System.out.println("Report exported to: " + filePath);

        } catch (IOException e) {
            System.err.println("Failed to write CSV: " + e.getMessage());
        }
    }
}
