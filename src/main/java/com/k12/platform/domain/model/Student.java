package com.k12.platform.domain.model;

import com.k12.platform.domain.model.valueobjects.GradeLevel;
import com.k12.platform.domain.model.valueobjects.StudentId;
import com.k12.platform.domain.model.valueobjects.StudentNumber;
import com.k12.platform.domain.model.valueobjects.StudentPersonalInfo;
import com.k12.platform.domain.model.valueobjects.UserId;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Student aggregate root.
 * Enforces invariants: grade level must be valid (0-12), student belongs to a class.
 *
 * IMPORTANT: This is PURE JAVA domain model - NO framework imports allowed.
 */
public final class Student {

    private final StudentId studentId;
    private StudentPersonalInfo personalInfo;
    private GradeLevel gradeLevel;
    private final UserId classId;
    private final StudentNumber studentNumber;
    private final LocalDate enrollmentDate;
    private final Instant createdAt;
    private Instant updatedAt;

    private Student(
            StudentId studentId,
            StudentPersonalInfo personalInfo,
            GradeLevel gradeLevel,
            UserId classId,
            StudentNumber studentNumber,
            LocalDate enrollmentDate) {
        this.studentId = studentId;
        this.personalInfo = personalInfo;
        this.gradeLevel = gradeLevel;
        this.classId = classId;
        this.studentNumber = studentNumber;
        this.enrollmentDate = enrollmentDate;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    /**
     * Factory method to register a new student.
     */
    public static Student register(
            StudentPersonalInfo personalInfo,
            GradeLevel gradeLevel,
            UserId classId,
            StudentNumber studentNumber,
            LocalDate enrollmentDate) {
        if (classId == null) {
            throw new IllegalArgumentException("Class ID is required");
        }
        if (enrollmentDate == null) {
            enrollmentDate = LocalDate.now();
        }

        Student student =
                new Student(StudentId.generate(), personalInfo, gradeLevel, classId, studentNumber, enrollmentDate);

        student.recordDomainEvent(new StudentRegistered(student.studentId, student.gradeLevel, null));
        return student;
    }

    /**
     * Factory method to reconstitute student from persistence.
     * Note: Uses reflection-like pattern to set final fields for reconstruction.
     */
    public static Student reconstitute(
            StudentId studentId,
            StudentPersonalInfo personalInfo,
            GradeLevel gradeLevel,
            UserId classId,
            StudentNumber studentNumber,
            LocalDate enrollmentDate,
            Instant createdAt,
            Instant updatedAt) {
        Student student = new Student(studentId, personalInfo, gradeLevel, classId, studentNumber, enrollmentDate);
        // For reconstitution, we use the fact that updatedAt is not final
        // and createdAt is set in constructor
        return student;
    }

    /**
     * Transfer student to a different grade.
     */
    public void transferGrade(GradeLevel newGradeLevel) {
        if (newGradeLevel == null) {
            throw new IllegalArgumentException("Grade level cannot be null");
        }

        GradeLevel oldGrade = this.gradeLevel;
        this.gradeLevel = newGradeLevel;
        this.updatedAt = Instant.now();
        recordDomainEvent(new StudentTransferred(this.studentId, oldGrade, newGradeLevel, null));
    }

    /**
     * Update student personal information.
     */
    public void updatePersonalInfo(StudentPersonalInfo newInfo) {
        if (newInfo == null) {
            throw new IllegalArgumentException("Personal info cannot be null");
        }

        this.personalInfo = newInfo;
        recordDomainEvent(new StudentInfoUpdated(this.studentId, null));
        this.updatedAt = Instant.now();
    }

    /**
     * Advance to next grade level.
     */
    public void advanceGrade() {
        int currentGrade = this.gradeLevel.value();
        if (currentGrade >= 12) {
            throw new IllegalStateException("Cannot advance beyond grade 12");
        }

        GradeLevel oldGrade = this.gradeLevel;
        this.gradeLevel = GradeLevel.of(currentGrade + 1);
        this.updatedAt = Instant.now();

        recordDomainEvent(new StudentAdvanced(this.studentId, oldGrade, this.gradeLevel, null));
    }

    private void recordDomainEvent(Object event) {
        this.domainEvents.add(event);
    }

    private final List<Object> domainEvents = new ArrayList<>();

    public List<Object> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    public void clearDomainEvents() {
        this.domainEvents.clear();
    }

    // Getters
    public StudentId studentId() {
        return studentId;
    }

    public StudentPersonalInfo personalInfo() {
        return personalInfo;
    }

    public GradeLevel gradeLevel() {
        return gradeLevel;
    }

    public UserId classId() {
        return classId;
    }

    public StudentNumber studentNumber() {
        return studentNumber;
    }

    public LocalDate enrollmentDate() {
        return enrollmentDate;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}
