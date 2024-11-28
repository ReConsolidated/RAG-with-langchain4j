package io.github.reconsolidated.pwcgpasik.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * A Data Transfer Object (DTO) representing the response from the chat service.
 * Contains the generated answer and the timestamp when the response was created.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {

    /**
     * The generated answer to the user's question.
     */
    private String answer;

    /**
     * The document id.
     */
    private UUID documentId;
}
