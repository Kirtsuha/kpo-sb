package hse.finance.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FinancialSummaryDTO(LocalDate start, LocalDate finish, BigDecimal totalIncome, BigDecimal totalExpense,
                                  BigDecimal totalChange) {
}
