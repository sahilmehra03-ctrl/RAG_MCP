package com.example.support.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.support.TestAiConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestAiConfig.class)
class TicketTransitionIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void happyPathTransitions() throws Exception {
        String publicId = createTicket();
        transition(publicId, "IN_PROGRESS", status().isOk());
        transition(publicId, "RESOLVED", status().isOk());
        transition(publicId, "CLOSED", status().isOk());
    }

    @Test
    void illegalResolvedToOpen_returns409() throws Exception {
        String publicId = createTicket();
        transition(publicId, "IN_PROGRESS", status().isOk());
        transition(publicId, "RESOLVED", status().isOk());
        transition(publicId, "OPEN", status().isConflict())
                .andExpect(jsonPath("$.error").value("INVALID_TRANSITION"));
    }

    private String createTicket() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "title", "Payment failure",
                                "description", "Card declined",
                                "priority", "HIGH"))))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString())
                .get("publicId")
                .asText();
    }

    private org.springframework.test.web.servlet.ResultActions transition(
            String publicId, String status, org.springframework.test.web.servlet.ResultMatcher matcher)
            throws Exception {
        return mockMvc.perform(post("/api/tickets/" + publicId + "/transition")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("status", status))))
                .andExpect(matcher);
    }
}
