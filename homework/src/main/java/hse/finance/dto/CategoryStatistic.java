package hse.finance.dto;

import hse.finance.domain.Category;
import hse.finance.domain.TransactionType;

import java.math.BigDecimal;

public record CategoryStatistic(Category category, BigDecimal amount, float percentage, TransactionType type) {
}
