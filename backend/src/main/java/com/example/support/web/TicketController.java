package com.example.support.web;

import com.example.support.domain.TicketStatus;
import com.example.support.service.TicketService;
import com.example.support.web.dto.AddCommentRequest;
import com.example.support.web.dto.CreateTicketRequest;
import com.example.support.web.dto.TicketResponse;
import com.example.support.web.dto.TransitionRequest;
import com.example.support.web.dto.UpdateTicketRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    public TicketResponse create(@Valid @RequestBody CreateTicketRequest request) {
        return ticketService.create(request);
    }

    @GetMapping
    public List<TicketResponse> list(
            @RequestParam(required = false) TicketStatus status,
            @RequestParam(required = false) String q) {
        return ticketService.list(status, q);
    }

    @GetMapping("/{publicId}")
    public TicketResponse get(@PathVariable String publicId) {
        return ticketService.get(publicId);
    }

    @PatchMapping("/{publicId}")
    public TicketResponse update(@PathVariable String publicId, @Valid @RequestBody UpdateTicketRequest request) {
        return ticketService.update(publicId, request);
    }

    @PostMapping("/{publicId}/transition")
    public TicketResponse transition(
            @PathVariable String publicId, @Valid @RequestBody TransitionRequest request) {
        return ticketService.transition(publicId, request);
    }

    @PostMapping("/{publicId}/comments")
    public TicketResponse addComment(
            @PathVariable String publicId, @Valid @RequestBody AddCommentRequest request) {
        return ticketService.addComment(publicId, request);
    }
}
