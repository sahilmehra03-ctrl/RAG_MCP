package com.example.support;

import java.util.List;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestAiConfig {

    @Bean
    @Primary
    EmbeddingModel testEmbeddingModel() {
        return new EmbeddingModel() {
            @Override
            public EmbeddingResponse call(EmbeddingRequest request) {
                List<Embedding> embeddings = request.getInstructions().stream()
                        .map(text -> new Embedding(hashToVector(text), 0))
                        .toList();
                return new EmbeddingResponse(embeddings);
            }

            @Override
            public float[] embed(Document document) {
                return hashToVector(document.getText());
            }

            private float[] hashToVector(String text) {
                float[] v = new float[8];
                int hash = text == null ? 0 : text.hashCode();
                for (int i = 0; i < v.length; i++) {
                    v[i] = ((hash >> (i % 16)) & 0xFF) / 255.0f;
                }
                return v;
            }
        };
    }

    @Bean
    @Primary
    ChatModel testChatModel() {
        return prompt -> new ChatResponse(List.of(new Generation(new AssistantMessage("Based on TKT-1: payment gateway timeout."))));
    }

    @Bean
    @Primary
    VectorStore testVectorStore(EmbeddingModel embeddingModel) {
        return SimpleVectorStore.builder(embeddingModel).build();
    }
}
