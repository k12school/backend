package com.k12.platform.domain.model;

import com.k12.platform.domain.model.valueobjects.AcademicYear;
import com.k12.platform.domain.model.valueobjects.ClassId;
import com.k12.platform.domain.model.valueobjects.GradeLevel;
import java.util.List;
import java.util.Optional;

/**
 * Port for Class persistence operations.
 */
public interface ClassRepository {
    void save(Class clazz);

    Optional<Class> findById(ClassId classId);

    List<Class> findByGradeLevel(GradeLevel gradeLevel);

    List<Class> findByAcademicYear(AcademicYear academicYear);

    List<Class> findByGradeLevelAndAcademicYear(GradeLevel gradeLevel, AcademicYear academicYear);

    boolean existsByNameAndGradeLevelAndAcademicYear(String name, int gradeLevel, String academicYear);

    boolean existsById(ClassId classId);
}
