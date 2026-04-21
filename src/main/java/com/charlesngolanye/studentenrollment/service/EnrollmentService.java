package com.charlesngolanye.studentenrollment.service;

import com.charlesngolanye.studentenrollment.dao.CourseDAO;
import com.charlesngolanye.studentenrollment.dao.EnrollmentDAO;
import com.charlesngolanye.studentenrollment.dao.StudentDAO;
import com.charlesngolanye.studentenrollment.model.Course;
import com.charlesngolanye.studentenrollment.model.Enrollment;
import com.charlesngolanye.studentenrollment.model.Student;

import java.util.List;
import java.util.Optional;


public class EnrollmentService {
    private double MIN_GRADE = 0.0;
    private double MAX_GRADE = 100.0;

    private final StudentDAO studentDAO;
    private final CourseDAO courseDAO;
    private final EnrollmentDAO enrollmentDAO;

    public EnrollmentService(StudentDAO studentDAO, CourseDAO courseDAO, EnrollmentDAO enrollmentDAO) {
        this.studentDAO = studentDAO;
        this.courseDAO = courseDAO;
        this.enrollmentDAO = enrollmentDAO;
    }


    public void enrollStudent(int studentId, int courseId){

        // check if student exists
        Optional<Student> student = studentDAO.findById(studentId);
        if (student.isEmpty()) {
            //todo Explain optionals
            System.out.println("Student not found");
            return;
        }

        // check if course exists
        Optional<Course> courseOptional = courseDAO.findByCourseId(courseId);
        if (courseOptional.isEmpty()) {
            System.err.println("Course not found");
        }
        Course course = courseOptional.get();

        // check if student already enrolled
        boolean exists = enrollmentDAO.exists(studentId, courseId);
        if (exists) {
            System.err.println("Student is already enrolled in this course");
        }

        // check course capacity
        int currentCount = enrollmentDAO.countByCourse(courseId);
        if (currentCount >= course.getCapacity()) {
            System.err.println("The course is full");
        }

        // if all good, insert enrollment
        Enrollment enrollment = new Enrollment(studentId, courseId);
        enrollmentDAO.insert(enrollment);
    }


    public void assignGrade(double grade, int studentId, int courseId) {
        if (grade > MIN_GRADE && grade <= MAX_GRADE) {
            if (enrollmentDAO.exists(studentId, courseId)) {
                enrollmentDAO.assignGrade(studentId, courseId, grade);
            }else{
                System.out.println("Enrollment does not exist for student with id: " + studentId);
            }
        }
        else{
            System.out.println("Grade should be between 0 - 100");
        }
    }

    /**
     * Student Report
     * Get enrollments by student, For each -> fetch course info, combine into a readable report
     * @param studentId student id
     * @return list of enrolled students
     */
    public List<Enrollment> enrollmentByStudentId(int studentId){
        return enrollmentDAO.findEnrollmentByStudentId(studentId);
    }

    /**
     * list all enrolled students with grades; show the class average
     * Course Report
     * Get enrollments by course; Fetch student details, calculate the class average
     * @param courseId course id
     * @return list of  course enrollments with average grade
     */
    public void courseReport(int courseId){
       List<Enrollment> enrollmentList = enrollmentDAO.findEnrollmentByCourseId(courseId);
       int numOfEnrollmentsFrCourse = enrollmentDAO.countByCourse(courseId);
       int totalGrade = 0;
       //avg = totalGrade/numEnrollmentsForTheCOurse
       //get the grade
        for (Enrollment e : enrollmentList){
            Double grade = e.getGrade();
            totalGrade += grade;

            //get students
            Student studen = stu
        }

        int average = totalGrade / numOfEnrollmentsFrCourse;

    }

    /**
     * Top performers
     * Get all students
     * For each -> get all their enrollments, compute GPA(average grade)
     * Filter -> GPA > threshold
     * Map -> calculate -> filter pipeline
     * @param studentId
     * @param grade
     */
    public void topPerformers(int studentId, double grade){

    }



}
/*
 * add dependencies private and final
 * all dependencies injected using the constructor ...you create field, then a constructor
 * DAO methods use 'this.connection' -
 * The StudentService is the boss. It imports the DatabaseConfig to grab a connection, then 'injects' that connection into the DAO's constructor
 * In this case the boss is StudentEnrollment which is the entry point to the app
 */
