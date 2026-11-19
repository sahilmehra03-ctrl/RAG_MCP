package com.example.support.service;

import com.example.support.domain.Comment;
import com.example.support.domain.Ticket;
import com.example.support.domain.TicketPriority;
import com.example.support.domain.TicketStateMachine;
import com.example.support.domain.TicketStatus;
import com.example.support.rag.TicketRagIngestionService;
import com.example.support.repository.TicketRepository;
import com.example.support.web.dto.AddCommentRequest;
import com.example.support.web.dto.CreateTicketRequest;
import com.example.support.web.dto.TicketResponse;
import com.example.support.web.dto.TransitionRequest;
import com.example.support.web.dto.UpdateTicketRequest;
import com.example.support.web.error.InvalidTransitionException;
import com.example.support.web.error.NotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final TicketStateMachine stateMachine;
    private final TicketRagIngestionService ragIngestionService;

    public TicketService(
            TicketRepository ticketRepository,
            TicketStateMachine stateMachine,
            TicketRagIngestionService ragIngestionService) {
        this.ticketRepository = ticketRepository;
        this.stateMachine = stateMachine;
        this.ragIngestionService = ragIngestionService;
    }

    @Transactional
    public TicketResponse create(CreateTicketRequest request) {
        Ticket ticket = new Ticket();
        ticket.setTitle(request.title());
        ticket.setDescription(request.description());
        ticket.setPriority(request.priority() != null ? request.priority() : TicketPriority.MEDIUM);
        ticket.setAssignee(request.assignee());
        ticket.setCategory(request.category());
        ticket.setStatus(TicketStatus.OPEN);
        Ticket saved = ticketRepository.saveAndFlush(ticket);
        saved.setPublicId(formatPublicId(saved.getId()));
        saved = ticketRepository.save(saved);
        ragIngestionService.reingest(saved);
        return TicketResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<TicketResponse> list(TicketStatus status, String q) {
        return ticketRepository.search(status, q).stream().map(TicketResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public TicketResponse get(String publicId) {
        return TicketResponse.from(findByPublicId(publicId));
    }

    @Transactional
    public TicketResponse update(String publicId, UpdateTicketRequest request) {
        Ticket ticket = findByPublicId(publicId);
        if (request.title() != null) {
            ticket.setTitle(request.title());
        }
        if (request.description() != null) {
            ticket.setDescription(request.description());
        }
        if (request.priority() != null) {
            ticket.setPriority(request.priority());
        }
        if (request.assignee() != null) {
            ticket.setAssignee(request.assignee());
        }
        if (request.category() != null) {
            ticket.setCategory(request.category());
        }
        if (request.resolutionNotes() != null) {
            ticket.setResolutionNotes(request.resolutionNotes());
        }
        Ticket saved = ticketRepository.save(ticket);
        ragIngestionService.reingest(saved);
        return TicketResponse.from(saved);
    }

    @Transactional
    public TicketResponse transition(String publicId, TransitionRequest request) {
        Ticket ticket = findByPublicId(publicId);
        TicketStatus target = request.status();
        if (!stateMachine.canTransition(ticket.getStatus(), target)) {
            throw new InvalidTransitionException(ticket.getStatus(), target);
        }
        ticket.setStatus(target);
        Ticket saved = ticketRepository.save(ticket);
        ragIngestionService.reingest(saved);
        return TicketResponse.from(saved);
    }

    @Transactional
    public TicketResponse addComment(String publicId, AddCommentRequest request) {
        Ticket ticket = findByPublicId(publicId);
        Comment comment = new Comment();
        comment.setAuthor(request.author());
        comment.setBody(request.body());
        ticket.addComment(comment);
        Ticket saved = ticketRepository.save(ticket);
        ragIngestionService.reingest(saved);
        return TicketResponse.from(saved);
    }

    private Ticket findByPublicId(String publicId) {
        return ticketRepository.findByPublicId(publicId)
                .orElseThrow(() -> new NotFoundException("Ticket not found: " + publicId));
    }

    private static String formatPublicId(Long id) {
        return "TKT-" + id;
    }
}
