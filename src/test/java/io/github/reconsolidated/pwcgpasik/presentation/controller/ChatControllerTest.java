package io.github.reconsolidated.pwcgpasik.presentation.controller;

import io.github.reconsolidated.pwcgpasik.application.ChatService;
import io.github.reconsolidated.pwcgpasik.domain.exceptions.DocumentNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChatController.class)
class ChatControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatService chatService;

    private UUID documentId;
    private String question;

    @BeforeEach
    void setUp() {
        documentId = UUID.randomUUID();
        question = "What is the content of the document?";
    }

    @Test
    void shouldReturnChatResponseSuccessfully() throws Exception {
        // Given
        String answer = "This is the document content.";
        Mockito.when(chatService.getAnswer(eq(documentId), eq(question)))
                .thenReturn(answer);

        // When & Then
        mockMvc.perform(post("/api/chat")
                        .param("documentId", documentId.toString())
                        .param("question", question)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer").value(answer))
                .andExpect(jsonPath("$.documentId").exists());
    }

    @Test
    void shouldReturnBadRequestWhenDocumentIdIsInvalid() throws Exception {
        // Given
        String invalidDocumentId = "invalid-uuid";

        // When & Then
        mockMvc.perform(post("/api/chat")
                        .param("documentId", invalidDocumentId)
                        .param("question", question)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnNotFoundWhenChatServiceThrowsException() throws Exception {
        // Given
        Mockito.when(chatService.getAnswer(eq(documentId), eq(question)))
                .thenThrow(new DocumentNotFoundException("Document not found"));

        // When & Then
        mockMvc.perform(post("/api/chat")
                        .param("documentId", documentId.toString())
                        .param("question", question)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Document not found"));
    }

    @Test
    void shouldReturnBadRequestWhenQuestionIsMissing() throws Exception {
        mockMvc.perform(post("/api/chat")
                        .param("documentId", documentId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenQuestionIsEmpty() throws Exception {
        mockMvc.perform(post("/api/chat")
                        .param("documentId", documentId.toString())
                        .param("question", "")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldHandleLargeQuestion() throws Exception {
        String largeQuestion = "a".repeat(10000); // 10,000 znaków

        Mockito.when(chatService.getAnswer(eq(documentId), eq(largeQuestion)))
                .thenReturn("Large question processed");

        mockMvc.perform(post("/api/chat")
                        .param("documentId", documentId.toString())
                        .param("question", largeQuestion)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer").value("Large question processed"))
                .andExpect(jsonPath("$.documentId").exists());
    }

    @Test
    void shouldReturnBadRequestForInvalidJson() throws Exception {
        String invalidJson = "{ \"documentId\": , \"question\": \"What is this?\" }";

        mockMvc.perform(post("/api/chat")
                        .content(invalidJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


}
