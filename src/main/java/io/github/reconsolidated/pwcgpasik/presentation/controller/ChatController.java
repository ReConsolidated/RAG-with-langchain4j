package io.github.reconsolidated.pwcgpasik.presentation.controller;

import io.github.reconsolidated.pwcgpasik.application.ChatService;
import io.github.reconsolidated.pwcgpasik.presentation.dto.ChatResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    /**
     * Endpoint for interacting with a document.
     *
     * @param documentId The ID of the document in UUID format.
     * @param question   The user's question about the document.
     * @return A response containing the generated answer and the response time.
     */
    @PostMapping
    public ResponseEntity<ChatResponse> chatWithDocument(
            @RequestParam String documentId,
            @RequestParam String question
    ) {
        UUID documentUuid = UUID.fromString(documentId);
        String answer = chatService.getAnswer(documentUuid, question);
        ChatResponse response = new ChatResponse(answer, documentUuid);
        return ResponseEntity.ok(response);
    }
}
