package hse.finance.dto;

import java.time.LocalDate;
import java.util.List;

public record CategorySummaryDTO(LocalDate start, LocalDate finish, List<CategoryStatistic> categoryAnalytics) {
}
