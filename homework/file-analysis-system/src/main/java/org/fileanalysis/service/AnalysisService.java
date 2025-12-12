package org.fileanalysis.service;

import org.fileanalysis.dto.AnalysisRequestDto;
import org.fileanalysis.dto.AnalysisReportDto;
import org.fileanalysis.dto.WorkReportsSummaryDto;
import org.fileanalysis.model.AnalysisReport;
import org.fileanalysis.repository.AnalysisReportRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnalysisService {

    private final AnalysisReportRepository repository;
    private final RestTemplate restTemplate;
    private final String storageBaseUrl;

    public AnalysisService(AnalysisReportRepository repository,
                           RestTemplate restTemplate,
                           @Value("${storage.base-url}") String storageBaseUrl) {
        this.repository = repository;
        this.restTemplate = restTemplate;
        this.storageBaseUrl = storageBaseUrl;
    }

    public AnalysisReportDto analyzeSubmission(AnalysisRequestDto request) {
        var existing = repository.findBySubmissionId(request.getSubmissionId());
        if (existing.isPresent()) {
            return toDto(existing.get());
        }

        String currentText = fetchSubmissionText(request.getSubmissionId());

        List<AnalysisReport> previousReports =
                repository.findByAssignmentIdOrderByAnalyzedAtAsc(request.getAssignmentId());

        String plagiarizedWith = null;

        for (AnalysisReport prev : previousReports) {
            String prevText = fetchSubmissionText(prev.getSubmissionId());
            if (prevText.equals(currentText)) {
                plagiarizedWith = prev.getSubmissionId();
                break;
            }
        }

        AnalysisReport report = new AnalysisReport();
        report.setSubmissionId(request.getSubmissionId());
        report.setAssignmentId(request.getAssignmentId());
        report.setStudentIdentifier(request.getStudentIdentifier());
        report.setPlagiarized(plagiarizedWith != null);
        report.setPlagiarizedWithSubmissionId(plagiarizedWith);
        report.setAnalyzedAt(LocalDateTime.now());

        repository.save(report);

        return toDto(report);
    }

    private String fetchSubmissionText(String submissionId) {
        try {
            byte[] bytes = restTemplate.getForObject(
                    storageBaseUrl + "/internal/submissions/" + submissionId + "/file",
                    byte[].class
            );
            if (bytes == null) {
                return "";
            }
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (RestClientException e) {
            return "";
        }
    }

    public AnalysisReportDto getReportForSubmission(String submissionId) {
        AnalysisReport report = repository.findBySubmissionId(submissionId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found for submission " + submissionId));
        return toDto(report);
    }

    public WorkReportsSummaryDto getReportsForAssignment(String assignmentId) {
        List<AnalysisReport> reports =
                repository.findByAssignmentIdOrderByAnalyzedAtAsc(assignmentId);

        List<AnalysisReportDto> dtos = reports.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return new WorkReportsSummaryDto(assignmentId, dtos);
    }

    private AnalysisReportDto toDto(AnalysisReport report) {
        return new AnalysisReportDto(
                report.getId(),
                report.getSubmissionId(),
                report.getAssignmentId(),
                report.getStudentIdentifier(),
                report.isPlagiarized(),
                report.getPlagiarizedWithSubmissionId(),
                report.getAnalyzedAt()
        );
    }
}
