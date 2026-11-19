package com.example.support.web.dto;

import java.util.List;

public record ErrorResponse(String error, String message, List<FieldError> fieldErrors) {

    public record FieldError(String field, String message) {}
}
