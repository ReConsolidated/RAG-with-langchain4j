package io.github.reconsolidated.pwcgpasik.application;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.filter.Filter;
import io.github.reconsolidated.pwcgpasik.domain.exceptions.DocumentNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChatServiceTest {

    private EmbeddingStore<TextSegment> embeddingStore;
    private EmbeddingModel embeddingModel;
    private ChatAgentWrapper documentChatAgent;
    private ContentRetriever contentRetriever;
    private ChatService chatService;

    @BeforeEach
    void setUp() {
        embeddingStore = mock(EmbeddingStore.class);
        embeddingModel = mock(EmbeddingModel.class);
        documentChatAgent = mock(ChatAgentWrapper.class);
        contentRetriever = mock(ContentRetriever.class);
        chatService = spy(new ChatService(embeddingStore, embeddingModel, documentChatAgent));
    }

    @Test
    void shouldReturnAnswerWhenContentIsFound() {
        // Given
        UUID documentId = UUID.randomUUID();
        String question = "What is the document about?";
        String mockContentText = "This is a mock content from the document.";
        String expectedAnswer = "This is the generated answer.";

        TextSegment textSegment = TextSegment.from(mockContentText);
        Content mockContent = Content.from(textSegment);

        // Mock behavior
        doReturn(contentRetriever).when(chatService).getContentRetriever(any(Filter.class));
        when(contentRetriever.retrieve(any(Query.class))).thenReturn(List.of(mockContent));
        when(documentChatAgent.answer(question, mockContentText)).thenReturn(expectedAnswer);

        // When
        String answer = chatService.getAnswer(documentId, question);

        // Then
        assertEquals(expectedAnswer, answer);

        // Verify retriever interactions
        ArgumentCaptor<Filter> filterCaptor = ArgumentCaptor.forClass(Filter.class);
        verify(chatService).getContentRetriever(filterCaptor.capture());
        assertTrue(filterCaptor.getValue().toString().contains("documentId"));
    }

    @Test
    void shouldThrowDocumentNotFoundExceptionWhenNoContentIsFound() {
        // Given
        UUID documentId = UUID.randomUUID();
        String question = "What is the document about?";

        // Mock behavior
        doReturn(contentRetriever).when(chatService).getContentRetriever(any(Filter.class));
        when(contentRetriever.retrieve(any(Query.class))).thenReturn(Collections.emptyList());

        // When & Then
        assertThrows(DocumentNotFoundException.class, () -> chatService.getAnswer(documentId, question));
    }
}
