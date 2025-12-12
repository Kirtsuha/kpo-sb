package org.filestorage.dto;

import jakarta.validation.constraints.NotBlank;

public class SubmissionRequest {

    @NotBlank
    private String studentName;

    @NotBlank
    private String studentIdentifier;

    @NotBlank
    private String assignmentId;

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
}
