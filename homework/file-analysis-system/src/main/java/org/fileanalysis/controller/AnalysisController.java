package org.fileanalysis.controller;

import org.fileanalysis.dto.AnalysisRequestDto;
import org.fileanalysis.dto.AnalysisReportDto;
import org.fileanalysis.dto.WorkReportsSummaryDto;
import org.fileanalysis.service.AnalysisService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal")
public class AnalysisController {

    private final AnalysisService analysisService;

    public AnalysisController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    /**
     * Запускает анализ конкретной сдачи.
     * Вызывается из API Gateway после успешной загрузки файла в File Storage Service.
     */
    @PostMapping("/analysis")
    public ResponseEntity<AnalysisReportDto> runAnalysis(@RequestBody AnalysisRequestDto request) {
        AnalysisReportDto report = analysisService.analyzeSubmission(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(report);
    }

    /**
     * Получить отчёт по конкретной сдаче.
     */
    @GetMapping("/reports/submissions/{submissionId}")
    public AnalysisReportDto getReportForSubmission(@PathVariable String submissionId) {
        return analysisService.getReportForSubmission(submissionId);
    }

    /**
     * Получить все отчёты по заданию (для преподавателя).
     */
    @GetMapping("/works/{assignmentId}/reports")
    public WorkReportsSummaryDto getReportsForAssignment(@PathVariable String assignmentId) {
        return analysisService.getReportsForAssignment(assignmentId);
    }
}
