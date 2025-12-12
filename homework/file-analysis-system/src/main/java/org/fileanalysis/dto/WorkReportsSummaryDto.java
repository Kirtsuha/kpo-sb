package org.fileanalysis.dto;

import java.util.List;

public class WorkReportsSummaryDto {

    private String assignmentId;
    private List<AnalysisReportDto> reports;

    public WorkReportsSummaryDto() {
    }

    public WorkReportsSummaryDto(String assignmentId, List<AnalysisReportDto> reports) {
        this.assignmentId = assignmentId;
        this.reports = reports;
    }

    public String getAssignmentId() {
        return assignmentId;
    }

    public List<AnalysisReportDto> getReports() {
        return reports;
    }
}
