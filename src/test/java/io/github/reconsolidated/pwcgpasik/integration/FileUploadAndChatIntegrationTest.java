package io.github.reconsolidated.pwcgpasik.integration;

import io.github.reconsolidated.pwcgpasik.PwcGpasikApplication;
import io.github.reconsolidated.pwcgpasik.application.ChatAgentWrapper;
import io.github.reconsolidated.pwcgpasik.application.DocumentChatAgent;
import io.github.reconsolidated.pwcgpasik.presentation.dto.ChatResponse;
import io.github.reconsolidated.pwcgpasik.presentation.dto.UploadResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FileUploadAndChatIntegrationTest {

    @Configuration
    @Import(PwcGpasikApplication.class)
    public static class TestConfig
    {
        @Bean
        @Primary
        public ChatAgentWrapper chatAgentWrapper() {
            DocumentChatAgent agent = mock(DocumentChatAgent.class);
            when(agent.answer(any(), any())).thenReturn("Anything with word 'important'.");
            return new ChatAgentWrapper(agent);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldUploadFileAndRetrieveAnswer() throws Exception {
        String fileContent = "This document contains the word 'important'.";
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test.txt",
                MediaType.TEXT_PLAIN_VALUE,
                fileContent.getBytes()
        );

        MvcResult uploadResult = mockMvc.perform(multipart("/api/upload")
                        .file(mockFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("File uploaded successfully"))
                .andReturn();

        // Extract documentId from the response
        String uploadResponseJson = uploadResult.getResponse().getContentAsString();
        UploadResponse uploadResponse = new com.fasterxml.jackson.databind.ObjectMapper()
                .readValue(uploadResponseJson, UploadResponse.class);
        UUID documentId = uploadResponse.getDocumentId();

        // 3. Ask a question about the document
        String question = "What does the document contain?";
        MvcResult chatResult = mockMvc.perform(post("/api/chat")
                        .param("documentId", documentId.toString())
                        .param("question", question)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer").exists())
                .andReturn();

        // Extract and verify the response
        String chatResponseJson = chatResult.getResponse().getContentAsString();
        ChatResponse chatResponse = new com.fasterxml.jackson.databind.ObjectMapper()
                .readValue(chatResponseJson, ChatResponse.class);

        assertThat(chatResponse.getAnswer()).contains("important");
    }
}
