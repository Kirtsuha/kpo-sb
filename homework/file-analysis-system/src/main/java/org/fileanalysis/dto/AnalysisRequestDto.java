package org.fileanalysis.dto;

public class AnalysisRequestDto {

    private String submissionId;
    private String assignmentId;
    private String studentIdentifier;

    public AnalysisRequestDto() {
    }

    public AnalysisRequestDto(String submissionId, String assignmentId, String studentIdentifier) {
        this.submissionId = submissionId;
        this.assignmentId = assignmentId;
        this.studentIdentifier = studentIdentifier;
    }

    public String getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(String submissionId) {
        this.submissionId = submissionId;
    }

    public String getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(String assignmentId) {
        this.assignmentId = assignmentId;
    }

    public String getStudentIdentifier() {
        return studentIdentifier;
    }

    public void setStudentIdentifier(String studentIdentifier) {
        this.studentIdentifier = studentIdentifier;
    }
}
