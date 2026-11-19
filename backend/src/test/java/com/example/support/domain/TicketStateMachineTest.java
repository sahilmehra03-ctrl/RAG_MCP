package com.example.support.domain;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TicketStateMachineTest {

    private final TicketStateMachine machine = new TicketStateMachine();

    @Test
    void open_toInProgress_allowed() {
        assertTrue(machine.canTransition(TicketStatus.OPEN, TicketStatus.IN_PROGRESS));
    }

    @Test
    void resolved_toOpen_rejected() {
        assertFalse(machine.canTransition(TicketStatus.RESOLVED, TicketStatus.OPEN));
    }

    @Test
    void closed_toOpen_rejected() {
        assertFalse(machine.canTransition(TicketStatus.CLOSED, TicketStatus.OPEN));
    }

    @Test
    void cancelled_toOpen_rejected() {
        assertFalse(machine.canTransition(TicketStatus.CANCELLED, TicketStatus.OPEN));
    }
}
