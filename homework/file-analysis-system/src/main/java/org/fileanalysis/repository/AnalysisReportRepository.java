package org.fileanalysis.repository;

import org.fileanalysis.model.AnalysisReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AnalysisReportRepository extends JpaRepository<AnalysisReport, String> {

    Optional<AnalysisReport> findBySubmissionId(String submissionId);

    List<AnalysisReport> findByAssignmentIdOrderByAnalyzedAtAsc(String assignmentId);
}
