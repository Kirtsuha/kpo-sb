package org.fileanalysis.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "analysis_reports")
public class AnalysisReport {

    @Id
    @Column(nullable = false, updatable = false)
    private String id;

    @Column(nullable = false)
    private String submissionId;

    @Column(nullable = false)
    private String assignmentId;

    @Column(nullable = false)
    private String studentIdentifier;

    @Column(nullable = false)
    private boolean plagiarized;

    @Column
    private String plagiarizedWithSubmissionId;

    @Column(nullable = false)
    private LocalDateTime analyzedAt;

    public AnalysisReport() {
        this.id = UUID.randomUUID().toString();
    }

    // getters / setters

    public String getId() {
        return id;
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

    public boolean isPlagiarized() {
        return plagiarized;
    }

    public void setPlagiarized(boolean plagiarized) {
        this.plagiarized = plagiarized;
    }

    public String getPlagiarizedWithSubmissionId() {
        return plagiarizedWithSubmissionId;
    }

    public void setPlagiarizedWithSubmissionId(String plagiarizedWithSubmissionId) {
        this.plagiarizedWithSubmissionId = plagiarizedWithSubmissionId;
    }

    public LocalDateTime getAnalyzedAt() {
        return analyzedAt;
    }

    public void setAnalyzedAt(LocalDateTime analyzedAt) {
        this.analyzedAt = analyzedAt;
    }
}
