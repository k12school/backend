package com.k12.platform.domain.port;

import com.k12.platform.domain.model.TeacherClassAssignment;
import com.k12.platform.domain.model.valueobjects.AssociationId;
import com.k12.platform.domain.model.valueobjects.ClassId;
import com.k12.platform.domain.model.valueobjects.UserId;
import java.util.List;
import java.util.Optional;

/**
 * Port for TeacherClassAssignment persistence operations.
 */
public interface TeacherClassAssignmentRepository {
    void save(TeacherClassAssignment assignment);

    Optional<TeacherClassAssignment> findById(AssociationId assignmentId);

    List<TeacherClassAssignment> findByTeacherId(UserId teacherId);

    List<TeacherClassAssignment> findByClassId(ClassId classId);

    Optional<TeacherClassAssignment> findByTeacherIdAndClassId(UserId teacherId, ClassId classId);

    boolean existsByTeacherIdAndClassId(UserId teacherId, ClassId classId);

    void delete(AssociationId assignmentId);
}
