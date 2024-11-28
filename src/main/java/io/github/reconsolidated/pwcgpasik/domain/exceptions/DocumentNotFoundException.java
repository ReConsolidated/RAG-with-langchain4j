package io.github.reconsolidated.pwcgpasik.domain.exceptions;

/**
 * Custom exception thrown when a document with the specified ID is not found.
 */
public class DocumentNotFoundException extends RuntimeException {

    /**
     * Constructs a new DocumentNotFoundException with the specified detail message.
     *
     * @param message the detail message
     */
    public DocumentNotFoundException(String message) {
        super(message);
    }
}
