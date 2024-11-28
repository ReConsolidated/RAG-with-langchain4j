package io.github.reconsolidated.pwcgpasik.application;

import dev.langchain4j.data.document.Document;
import io.github.reconsolidated.pwcgpasik.domain.exceptions.FileProcessingException;
import io.github.reconsolidated.pwcgpasik.domain.exceptions.UnsupportedFileFormatException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FileUploadServiceTest {

    private DocumentIngestor documentIngestor;
    private FileUploadService fileUploadService;

    @BeforeEach
    void setUp() {
        documentIngestor = mock(DocumentIngestor.class);
        fileUploadService = new FileUploadService(documentIngestor);
    }

    @Test
    void shouldProcessTxtFileSuccessfully() throws IOException {
        // Given
        MultipartFile mockFile = mock(MultipartFile.class);
        String fileContent = "Sample text content";
        UUID expectedUUID = UUID.randomUUID();

        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getOriginalFilename()).thenReturn("sample.txt");
        when(mockFile.getBytes()).thenReturn(fileContent.getBytes());
        when(documentIngestor.ingestDocument(any(Document.class))).thenReturn(expectedUUID);

        // When
        UUID result = fileUploadService.uploadFile(mockFile);

        // Then
        assertEquals(expectedUUID, result);
        ArgumentCaptor<Document> documentCaptor = ArgumentCaptor.forClass(Document.class);
        verify(documentIngestor).ingestDocument(documentCaptor.capture());
        assertEquals(fileContent, documentCaptor.getValue().text());
    }

    @Test
    void shouldThrowUnsupportedFileFormatExceptionForUnknownFileType() {
        // Given
        MultipartFile mockFile = mock(MultipartFile.class);

        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getOriginalFilename()).thenReturn("sample.unknown");

        // When & Then
        assertThrows(UnsupportedFileFormatException.class, () -> fileUploadService.uploadFile(mockFile));
    }

    @Test
    void shouldThrowFileProcessingExceptionForIOException() throws IOException {
        // Given
        MultipartFile mockFile = mock(MultipartFile.class);

        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getOriginalFilename()).thenReturn("sample.txt");
        when(mockFile.getBytes()).thenThrow(new IOException("Simulated IO error"));

        // When & Then
        FileProcessingException exception = assertThrows(FileProcessingException.class, () -> fileUploadService.uploadFile(mockFile));
        assertTrue(exception.getMessage().contains("sample.txt"));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionForEmptyFile() {
        // Given
        MultipartFile mockFile = mock(MultipartFile.class);

        when(mockFile.isEmpty()).thenReturn(true);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> fileUploadService.uploadFile(mockFile));
    }
}
