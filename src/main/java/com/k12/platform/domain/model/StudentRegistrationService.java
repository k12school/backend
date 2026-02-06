package com.k12.platform.domain.model;

import com.k12.platform.domain.model.valueobjects.GradeLevel;
import com.k12.platform.domain.model.valueobjects.StudentId;
import com.k12.platform.domain.model.valueobjects.StudentNumber;
import com.k12.platform.domain.model.valueobjects.StudentPersonalInfo;
import com.k12.platform.domain.model.valueobjects.UserId;
import com.k12.platform.domain.port.StudentRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.Instant;
import java.time.LocalDate;

/**
 * Domain service for Student registration.
 * Ensures business rules around student registration.
 */
@ApplicationScoped
public class StudentRegistrationService {

    private final StudentRepository studentRepository;

    public StudentRegistrationService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    /**
     * Register a new student with uniqueness validation.
     */
    public Student registerStudent(
            StudentPersonalInfo personalInfo,
            GradeLevel gradeLevel,
            UserId classId,
            StudentNumber studentNumber,
            LocalDate enrollmentDate) {
        Student student = Student.register(personalInfo, gradeLevel, classId, studentNumber, enrollmentDate);
        studentRepository.save(student);
        return student;
    }

    /**
     * Reconstitute a student from persistence.
     */
    public Student reconstituteStudent(
            StudentId studentId,
            StudentPersonalInfo personalInfo,
            GradeLevel gradeLevel,
            UserId classId,
            StudentNumber studentNumber,
            LocalDate enrollmentDate) {
        return Student.reconstitute(
                studentId,
                personalInfo,
                gradeLevel,
                classId,
                studentNumber,
                enrollmentDate,
                Instant.now(),
                Instant.now());
    }
}
