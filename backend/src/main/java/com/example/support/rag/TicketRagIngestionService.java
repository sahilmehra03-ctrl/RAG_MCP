package com.example.support.rag;

import com.example.support.domain.Comment;
import com.example.support.domain.Ticket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

@Service
public class TicketRagIngestionService {

    private static final int CHUNK_SIZE = 512;
    private static final int CHUNK_OVERLAP = 50;

    private final VectorStore vectorStore;
    private final TokenTextSplitter splitter;

    public TicketRagIngestionService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
        this.splitter = new TokenTextSplitter(CHUNK_SIZE, CHUNK_OVERLAP, 5, 10000, true);
    }

    public void reingest(Ticket ticket) {
        deleteByTicketId(ticket.getPublicId());
        String fullText = buildCanonicalText(ticket);
        Document source = new Document(fullText, baseMetadata(ticket));
        List<Document> chunks = splitter.apply(List.of(source));
        List<Document> withMeta = new ArrayList<>();
        for (int i = 0; i < chunks.size(); i++) {
            Map<String, Object> meta = new HashMap<>(baseMetadata(ticket));
            meta.put("chunkIndex", i);
            withMeta.add(new Document(chunks.get(i).getText(), meta));
        }
        if (!withMeta.isEmpty()) {
            vectorStore.add(withMeta);
        }
    }

    private void deleteByTicketId(String publicId) {
        var existing = vectorStore.similaritySearch(
                SearchRequest.builder().query(publicId).topK(100).build());
        List<String> idsToDelete = existing.stream()
                .filter(doc -> publicId.equals(doc.getMetadata().get("ticketId")))
                .map(Document::getId)
                .filter(id -> id != null)
                .toList();
        if (!idsToDelete.isEmpty()) {
            vectorStore.delete(idsToDelete);
        }
    }

    private Map<String, Object> baseMetadata(Ticket ticket) {
        Map<String, Object> meta = new HashMap<>();
        meta.put("ticketId", ticket.getPublicId());
        meta.put("status", ticket.getStatus().name());
        meta.put("priority", ticket.getPriority().name());
        meta.put("assignee", ticket.getAssignee() != null ? ticket.getAssignee() : "");
        meta.put("category", ticket.getCategory() != null ? ticket.getCategory() : "");
        return meta;
    }

    private String buildCanonicalText(Ticket ticket) {
        StringBuilder sb = new StringBuilder();
        sb.append("Title: ").append(ticket.getTitle()).append('\n');
        sb.append("Description: ").append(ticket.getDescription()).append('\n');
        if (ticket.getResolutionNotes() != null && !ticket.getResolutionNotes().isBlank()) {
            sb.append("Resolution notes: ").append(ticket.getResolutionNotes()).append('\n');
        }
        for (Comment c : ticket.getComments()) {
            sb.append("Comment by ").append(c.getAuthor()).append(": ").append(c.getBody()).append('\n');
        }
        return sb.toString();
    }
}
