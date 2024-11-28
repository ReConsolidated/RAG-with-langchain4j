package io.github.reconsolidated.pwcgpasik.application;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.filter.Filter;
import dev.langchain4j.store.embedding.filter.MetadataFilterBuilder;
import io.github.reconsolidated.pwcgpasik.domain.exceptions.DocumentNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private final EmbeddingStore<TextSegment> embeddingStore;
    private final EmbeddingModel embeddingModel;
    private final ChatAgentWrapper documentChatAgent;

    public ChatService(@Qualifier(value = "embeddingStore") EmbeddingStore<TextSegment> embeddingStore,
                       EmbeddingModel embeddingModel,
                       ChatAgentWrapper documentChatAgent) {
        this.embeddingStore = embeddingStore;
        this.embeddingModel = embeddingModel;
        this.documentChatAgent = documentChatAgent;
    }

    /**
     * Processes the user's question in the context of the document's content.
     *
     * @param documentId The ID of the document
     * @param question   The user's question
     * @return The answer generated based on the document's content
     */
    public String getAnswer(UUID documentId, String question) {
        Query query = Query.from(question);

        Filter filter = MetadataFilterBuilder.metadataKey("documentId").isEqualTo(documentId);
        ContentRetriever retriever = getContentRetriever(filter);

        List<Content> contents = retriever.retrieve(query);

        if (contents.isEmpty()) {
            throw new DocumentNotFoundException("Document with id " + documentId + " not found");
        }

        String relevantContent = contents.stream()
                .map(Content::textSegment)
                .map(TextSegment::text)
                .collect(Collectors.joining("\n"));

        return documentChatAgent.answer(question, relevantContent);
    }

    ContentRetriever getContentRetriever(Filter filter) {
        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .filter(filter)
                .maxResults(10)
                .build();
    }
}
