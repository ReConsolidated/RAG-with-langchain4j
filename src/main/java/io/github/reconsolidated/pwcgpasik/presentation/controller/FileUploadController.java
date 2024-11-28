package io.github.reconsolidated.pwcgpasik.presentation.controller;

import io.github.reconsolidated.pwcgpasik.application.FileUploadService;
import io.github.reconsolidated.pwcgpasik.presentation.dto.UploadResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    private final FileUploadService fileUploadService;

    public FileUploadController(FileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }

    /**
     * Endpoint for file uploads.
     *
     * @param file File uploaded by the user
     * @return Response containing the operation status and document ID
     */
    @PostMapping
    public ResponseEntity<UploadResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        UUID documentId = fileUploadService.uploadFile(file);
        UploadResponse response = new UploadResponse(documentId, "File uploaded successfully");
        return ResponseEntity.ok(response);
    }
}
