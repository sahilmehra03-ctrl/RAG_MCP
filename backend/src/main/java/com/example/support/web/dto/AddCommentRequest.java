package com.example.support.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddCommentRequest(
        @NotBlank @Size(max = 100) String author,
        @NotBlank @Size(max = 5000) String body) {}
