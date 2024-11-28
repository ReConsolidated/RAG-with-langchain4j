package io.github.reconsolidated.pwcgpasik.presentation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Represents the response returned after a file upload operation.
 * Contains the unique identifier of the uploaded document and the status message.
 */
@Getter
@Setter
@NoArgsConstructor
public class UploadResponse {
    private UUID documentId; // Unique identifier of the uploaded document
    private String status;   // Status message of the upload operation

    /**
     * Constructs a new UploadResponse with the specified document ID and status message.
     *
     * @param documentId Unique identifier of the uploaded document.
     * @param status     Status message of the upload operation.
     */
    public UploadResponse(UUID documentId, String status) {
        this.documentId = documentId;
        this.status = status;
    }
}
