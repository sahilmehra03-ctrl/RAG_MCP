package com.example.support.rag;

import com.example.support.config.RagProperties;
import com.example.support.web.dto.AskResponse;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

@Service
public class TicketRagAskService {

    private static final String NO_MATCH =
            "No relevant tickets found in the knowledge base for your question.";
    private static final Pattern TICKET_ID_PATTERN = Pattern.compile("TKT-\\d+");

    private final VectorStore vectorStore;
    private final ChatClient chatClient;
    private final RagProperties ragProperties;

    public TicketRagAskService(VectorStore vectorStore, ChatModel chatModel, RagProperties ragProperties) {
        this.vectorStore = vectorStore;
        this.chatClient = ChatClient.builder(chatModel).build();
        this.ragProperties = ragProperties;
    }

    public AskResponse ask(String question) {
        List<Document> retrieved = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(question)
                        .topK(ragProperties.getTopK())
                        .similarityThreshold(ragProperties.getSimilarityThreshold())
                        .build());

        if (retrieved.isEmpty()) {
            return new AskResponse(NO_MATCH, List.of(), false);
        }

        Set<String> allowedIds = new LinkedHashSet<>();
        StringBuilder context = new StringBuilder();
        for (Document doc : retrieved) {
            Object ticketId = doc.getMetadata().get("ticketId");
            if (ticketId != null) {
                allowedIds.add(ticketId.toString());
            }
            context.append("---\n").append(doc.getText()).append('\n');
        }

        String system = """
                You answer support questions using ONLY the ticket excerpts below.
                Cite ticket IDs in the form TKT-#### when referencing a ticket.
                If the excerpts do not contain the answer, say you cannot answer from the tickets.
                Do not invent ticket IDs or facts.
                """;
        String user = "Context:\n" + context + "\nQuestion: " + question;

        String answer = chatClient.prompt()
                .system(system)
                .user(user)
                .call()
                .content();

        List<String> cited = filterCitations(answer, allowedIds);
        return new AskResponse(answer, cited, true);
    }

    private List<String> filterCitations(String answer, Set<String> allowedIds) {
        Matcher matcher = TICKET_ID_PATTERN.matcher(answer);
        List<String> cited = new ArrayList<>();
        while (matcher.find()) {
            String id = matcher.group();
            if (allowedIds.contains(id) && !cited.contains(id)) {
                cited.add(id);
            }
        }
        return cited;
    }
}
