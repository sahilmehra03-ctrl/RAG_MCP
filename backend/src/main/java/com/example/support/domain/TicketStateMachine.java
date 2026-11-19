package com.example.support.domain;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class TicketStateMachine {

    private final Map<TicketStatus, Set<TicketStatus>> allowed = new EnumMap<>(TicketStatus.class);

    public TicketStateMachine() {
        allowed.put(TicketStatus.OPEN, EnumSet.of(TicketStatus.IN_PROGRESS, TicketStatus.CANCELLED));
        allowed.put(TicketStatus.IN_PROGRESS, EnumSet.of(TicketStatus.RESOLVED, TicketStatus.CANCELLED));
        allowed.put(TicketStatus.RESOLVED, EnumSet.of(TicketStatus.CLOSED));
        allowed.put(TicketStatus.CLOSED, EnumSet.noneOf(TicketStatus.class));
        allowed.put(TicketStatus.CANCELLED, EnumSet.noneOf(TicketStatus.class));
    }

    public boolean canTransition(TicketStatus from, TicketStatus to) {
        if (from == null || to == null) {
            return false;
        }
        return allowed.getOrDefault(from, EnumSet.noneOf(TicketStatus.class)).contains(to);
    }
}
