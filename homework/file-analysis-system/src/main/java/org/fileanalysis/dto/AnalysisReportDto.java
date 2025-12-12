package org.fileanalysis.dto;

import java.time.LocalDateTime;

public class AnalysisReportDto {

    private String id;
    private String submissionId;
    private String assignmentId;
    private String studentIdentifier;
    private boolean plagiarized;
    private String plagiarizedWithSubmissionId;
    private LocalDateTime analyzedAt;

    public AnalysisReportDto() {
    }

    public AnalysisReportDto(String id,
                             String submissionId,
                             String assignmentId,
                             String studentIdentifier,
                             boolean plagiarized,
                             String plagiarizedWithSubmissionId,
                             LocalDateTime analyzedAt) {
        this.id = id;
        this.submissionId = submissionId;
        this.assignmentId = assignmentId;
        this.studentIdentifier = studentIdentifier;
        this.plagiarized = plagiarized;
        this.plagiarizedWithSubmissionId = plagiarizedWithSubmissionId;
        this.analyzedAt = analyzedAt;
    }

    public String getId() {
        return id;
    }

    public String getSubmissionId() {
        return submissionId;
    }

    public String getAssignmentId() {
        return assignmentId;
    }

    public String getStudentIdentifier() {
        return studentIdentifier;
    }

    public boolean isPlagiarized() {
        return plagiarized;
    }

    public String getPlagiarizedWithSubmissionId() {
        return plagiarizedWithSubmissionId;
    }

    public LocalDateTime getAnalyzedAt() {
        return analyzedAt;
    }
}
