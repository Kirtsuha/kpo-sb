package org.gateway.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.gateway.dto.SubmissionFullResponse;
import org.gateway.service.GatewayService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@Tag(name = "API Gateway")
public class GatewayController {

    private final GatewayService service;

    public GatewayController(GatewayService service) {
        this.service = service;
    }

    @Operation(
            summary = "Upload submission and analyze",
            requestBody = @RequestBody(
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(implementation = UploadSchema.class)
                    )
            )
    )
    @PostMapping(value = "/submissions", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SubmissionFullResponse> upload(
            @RequestPart("file") MultipartFile file,
            @RequestPart("meta") String meta
    ) throws Exception {
        return ResponseEntity.ok(
                service.uploadAndAnalyze(
                        file.getBytes(),
                        file.getOriginalFilename(),
                        meta
                )
        );
    }

    @GetMapping("/submissions/{id}")
    public Object getSubmission(@PathVariable String id) {
        return service.getSubmission(id);
    }

    @GetMapping("/submissions/{id}/analysis")
    public Object getAnalysis(@PathVariable String id) {
        return service.getAnalysis(id);
    }

    @GetMapping("/assignments/{assignmentId}/reports")
    public Object getReports(@PathVariable String assignmentId) {
        return service.getReports(assignmentId);
    }

    // Swagger helper schema
    static class UploadSchema {
        @Schema(type = "string", format = "binary")
        public MultipartFile file;

        @Schema(
                description = "Meta JSON",
                example = "{\"studentName\":\"Alice\",\"studentIdentifier\":\"alice01\",\"assignmentId\":\"HW1\"}"
        )
        public String meta;
    }
}
