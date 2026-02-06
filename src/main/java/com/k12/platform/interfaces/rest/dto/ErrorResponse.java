package com.k12.platform.interfaces.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * REST response DTO for errors.
 */
public record ErrorResponse(String message) {

}
