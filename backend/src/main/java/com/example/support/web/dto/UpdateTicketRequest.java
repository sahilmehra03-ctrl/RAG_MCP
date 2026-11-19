package com.example.support.web.dto;

import com.example.support.domain.TicketPriority;
import jakarta.validation.constraints.Size;

public record UpdateTicketRequest(
        @Size(max = 200) String title,
        @Size(max = 5000) String description,
        TicketPriority priority,
        @Size(max = 100) String assignee,
        @Size(max = 100) String category,
        @Size(max = 5000) String resolutionNotes) {}
