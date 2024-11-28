package io.github.reconsolidated.pwcgpasik.application;

import dev.langchain4j.data.document.Document;
import io.github.reconsolidated.pwcgpasik.domain.exceptions.FileProcessingException;
import io.github.reconsolidated.pwcgpasik.domain.exceptions.UnsupportedFileFormatException;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileUploadService {

    private final DocumentIngestor documentIngestor;

    /**
     * Processes the uploaded file and stores its content in the document ingestor.
     *
     * @param file the file uploaded by the user
     * @return the unique ID of the ingested document
     * @throws IllegalArgumentException if the file is empty
     * @throws UnsupportedFileFormatException if the file format is not supported
     * @throws FileProcessingException if an error occurs while processing the file
     */
    public UUID uploadFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("The file is empty");
        }

        String content;
        String fileType = getFileExtension(file);

        try {
            // Handle different file formats
            if ("pdf".equalsIgnoreCase(fileType)) {
                content = extractTextFromPdf(file); // Extract text from PDF
            } else if ("txt".equalsIgnoreCase(fileType)) {
                content = new String(file.getBytes()); // Read plain text file
            } else {
                throw new UnsupportedFileFormatException("Unsupported file format: " + fileType);
            }
        } catch (IOException e) {
            throw new FileProcessingException("Failed to process the file: " + file.getOriginalFilename(), e);
        }

        // Convert the file content into a Document object for ingestion
        Document langchainDocument = Document.from(content);

        // Ingest the document and return its unique identifier
        return documentIngestor.ingestDocument(langchainDocument);
    }

    /**
     * Extracts text from a PDF file.
     *
     * @param file the PDF file uploaded by the user
     * @return the extracted text content
     * @throws IOException if an error occurs while reading the PDF file
     */
    private String extractTextFromPdf(MultipartFile file) throws IOException {
        try (PDDocument pdfDocument = PDDocument.load(file.getInputStream())) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            return pdfStripper.getText(pdfDocument);
        }
    }

    /**
     * Retrieves the file extension from the original filename.
     *
     * @param file the uploaded file
     * @return the file extension (e.g., "txt" or "pdf")
     */
    private String getFileExtension(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf(".") + 1); // Extract extension after the last dot
        }
        return ""; // Return an empty string if no extension is found
    }
}
