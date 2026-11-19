package com.example.support.web.dto;

import java.util.List;

public record AskResponse(String answer, List<String> citedTicketIds, boolean grounded) {}
