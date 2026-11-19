package com.example.support.web.error;

import com.example.support.domain.TicketStatus;

public class InvalidTransitionException extends RuntimeException {

    private final TicketStatus from;
    private final TicketStatus to;

    public InvalidTransitionException(TicketStatus from, TicketStatus to) {
        super("Cannot transition from " + from + " to " + to);
        this.from = from;
        this.to = to;
    }

    public TicketStatus getFrom() {
        return from;
    }

    public TicketStatus getTo() {
        return to;
    }
}
