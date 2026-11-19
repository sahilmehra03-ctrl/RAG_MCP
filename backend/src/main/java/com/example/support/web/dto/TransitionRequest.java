package com.example.support.web.dto;

import com.example.support.domain.TicketStatus;
import jakarta.validation.constraints.NotNull;

public record TransitionRequest(@NotNull TicketStatus status) {}
