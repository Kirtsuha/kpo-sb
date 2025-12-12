package org.filestorage.controller;

import jakarta.validation.Valid;
import org.filestorage.dto.SubmissionRequest;
import org.filestorage.dto.SubmissionResponse;
import org.filestorage.model.Submission;
import org.filestorage.service.FileStorageService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/internal")
public class SubmissionController {

    private final FileStorageService fileStorageService;

    public SubmissionController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping(
            value = "/submissions",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<SubmissionResponse> upload(
            @RequestPart("file") MultipartFile file,
            @Valid @RequestPart("meta") SubmissionRequest meta
    ) {
        Submission saved = fileStorageService.saveSubmission(file, meta);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toResponse(saved));
    }

    @GetMapping("/submissions/{id}")
    public ResponseEntity<SubmissionResponse> getSubmission(@PathVariable UUID id) {
        Submission s = fileStorageService.getSubmission(id);
        return ResponseEntity.ok(toResponse(s));
    }

    @GetMapping("/submissions/{id}/file")
    public ResponseEntity<Resource> downloadFile(@PathVariable UUID id) {
        Submission s = fileStorageService.getSubmission(id);
        byte[] bytes = fileStorageService.downloadFile(s);
        ByteArrayResource resource = new ByteArrayResource(bytes);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename(s.getOriginalFileName())
                        .build()
        );

        MediaType mediaType;
        try {
            mediaType = MediaType.parseMediaType(s.getContentType());
        } catch (Exception e) {
            mediaType = MediaType.APPLICATION_OCTET_STREAM;
        }

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(bytes.length)
                .contentType(mediaType)
                .body(resource);
    }

    @GetMapping("/works/{assignmentId}/submissions")
    public ResponseEntity<List<SubmissionResponse>> getByAssignment(
            @PathVariable String assignmentId
    ) {
        List<Submission> list = fileStorageService.getSubmissionsByAssignment(assignmentId);
        List<SubmissionResponse> result = list.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    private SubmissionResponse toResponse(Submission s) {
        SubmissionResponse dto = new SubmissionResponse();
        dto.setId(s.getId());
        dto.setStudentName(s.getStudentName());
        dto.setStudentIdentifier(s.getStudentIdentifier());
        dto.setAssignmentId(s.getAssignmentId());
        dto.setOriginalFileName(s.getOriginalFileName());
        dto.setSizeBytes(s.getSizeBytes());
        dto.setUploadedAt(s.getUploadedAt());
        dto.setHasStorageInfo(true);
        return dto;
    }
}
