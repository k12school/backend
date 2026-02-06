package com.k12.platform.domain.port;

import com.k12.platform.domain.model.Student;
import com.k12.platform.domain.model.valueobjects.StudentId;
import com.k12.platform.domain.model.valueobjects.UserId;
import java.util.List;
import java.util.Optional;

/**
 * Port for Student persistence operations.
 */
public interface StudentRepository {
    void save(Student student);

    Optional<Student> findById(StudentId studentId);

    List<Student> findByClassId(UserId classId);

    List<Student> findByGradeLevel(int gradeLevel);

    boolean existsById(StudentId studentId);
}
