package com.k12.platform.interfaces.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * Request DTO for transferring student to different grade.
 */
public record TransferGradeRequest(
        @NotNull(message = "Grade level is required") @Pattern(regexp = "^(K|0|1[0-2]|[1-9])$", message = "Grade level must be K or 0-12")
                @JsonProperty("grade_level")
                String gradeLevel) {}
