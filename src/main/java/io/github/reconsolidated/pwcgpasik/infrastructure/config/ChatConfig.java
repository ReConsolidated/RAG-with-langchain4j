package io.github.reconsolidated.pwcgpasik.infrastructure.config;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.TokenWindowChatMemory;
import dev.langchain4j.model.Tokenizer;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class ChatConfig {
    @Bean
    public EmbeddingModel embeddingModel() {
        return new AllMiniLmL6V2EmbeddingModel();
    }

    @Bean // Used by Ingestor and Retriever
    public EmbeddingStore<TextSegment> embeddingStore() {
        return new InMemoryEmbeddingStore<>();
    }

    @Bean
    @Primary // Primary, so that @AiService-generated chat agent uses different embedding store than Ingestor and Retriever
    public EmbeddingStore<TextSegment> embeddingStoreModel() {
        return new InMemoryEmbeddingStore<>();
    }

    @Bean
    public ChatMemory chatMemory(Tokenizer tokenizer) {
        return TokenWindowChatMemory.withMaxTokens(1000, tokenizer);
    }
}
