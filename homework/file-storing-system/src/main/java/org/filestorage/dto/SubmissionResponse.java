package org.filestorage.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class SubmissionResponse {

    private UUID id;
    private String studentName;
    private String studentIdentifier;
    private String assignmentId;
    private String originalFileName;
    private Long sizeBytes;
    private LocalDateTime uploadedAt;
    private boolean hasStorageInfo;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentIdentifier() {
        return studentIdentifier;
    }

    public void setStudentIdentifier(String studentIdentifier) {
        this.studentIdentifier = studentIdentifier;
    }

    public String getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(String assignmentId) {
        this.assignmentId = assignmentId;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public Long getSizeBytes() {
        return sizeBytes;
    }

    public void setSizeBytes(Long sizeBytes) {
        this.sizeBytes = sizeBytes;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public boolean isHasStorageInfo() {
        return hasStorageInfo;
    }

    public void setHasStorageInfo(boolean hasStorageInfo) {
        this.hasStorageInfo = hasStorageInfo;
    }
}
