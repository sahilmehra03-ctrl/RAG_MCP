package com.example.support.web.dto;

import com.example.support.domain.Comment;
import com.example.support.domain.Ticket;
import com.example.support.domain.TicketPriority;
import com.example.support.domain.TicketStatus;
import java.time.Instant;
import java.util.List;

public record TicketResponse(
        String publicId,
        String title,
        String description,
        TicketStatus status,
        TicketPriority priority,
        String assignee,
        String category,
        String resolutionNotes,
        Instant createdAt,
        Instant updatedAt,
        List<CommentResponse> comments) {

    public static TicketResponse from(Ticket ticket) {
        List<CommentResponse> comments = ticket.getComments().stream()
                .map(CommentResponse::from)
                .toList();
        return new TicketResponse(
                ticket.getPublicId(),
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getStatus(),
                ticket.getPriority(),
                ticket.getAssignee(),
                ticket.getCategory(),
                ticket.getResolutionNotes(),
                ticket.getCreatedAt(),
                ticket.getUpdatedAt(),
                comments);
    }

    public record CommentResponse(Long id, String author, String body, Instant createdAt) {
        static CommentResponse from(Comment comment) {
            return new CommentResponse(
                    comment.getId(), comment.getAuthor(), comment.getBody(), comment.getCreatedAt());
        }
    }
}
