package com.k12.platform.domain.model;

import com.k12.platform.domain.model.valueobjects.AssociationId;
import com.k12.platform.domain.model.valueobjects.StudentId;
import com.k12.platform.domain.model.valueobjects.UserId;
import java.util.List;
import java.util.Optional;

/**
 * Port for ParentStudentAssociation persistence operations.
 */
public interface ParentStudentAssociationRepository {
    void save(ParentStudentAssociation association);

    Optional<ParentStudentAssociation> findById(AssociationId associationId);

    List<ParentStudentAssociation> findByParentId(UserId parentId);

    List<ParentStudentAssociation> findByStudentId(StudentId studentId);

    Optional<ParentStudentAssociation> findByParentIdAndStudentId(UserId parentId, StudentId studentId);

    boolean existsByParentIdAndStudentId(UserId parentId, StudentId studentId);

    void delete(AssociationId associationId);
}
