package com.k12.platform.infrastructure.persistence;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JPA entity for ParentStudentAssociation persistence.
 */
@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "parent_student_associations")
public class PanacheParentStudentAssociationEntity extends PanacheEntityBase {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "parent_id", nullable = false)
    private UUID parentId;

    @Column(name = "student_id", nullable = false)
    private UUID studentId;

    @Column(name = "relationship_type", nullable = false)
    private String relationshipType;

    @Column(name = "is_primary_contact", nullable = false)
    private boolean isPrimaryContact;

    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;
}
