package com.example.support.rag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.support.TestAiConfig;
import com.example.support.web.dto.AskResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestAiConfig.class)
class RagAskServiceIT {

    @Autowired
    private TicketRagAskService askService;

    @Test
    void emptyStore_returnsNoMatchWithoutFabrication() {
        AskResponse response = askService.ask("payment failures");
        assertFalse(response.grounded());
        assertTrue(response.citedTicketIds().isEmpty());
        assertEquals(
                "No relevant tickets found in the knowledge base for your question.",
                response.answer());
    }
}
