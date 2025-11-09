package hse.finance.command;

import hse.finance.domain.Category;
import hse.finance.domain.Operation;
import hse.finance.domain.TransactionType;
import hse.finance.dto.CategoryStatistic;
import hse.finance.dto.CategorySummaryDTO;
import hse.finance.service.OperationFacade;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CategorySummaryCommand implements Command<CategorySummaryDTO> {
    private final OperationFacade operationFacade;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final List<Category> categoryList;

    public CategorySummaryCommand(OperationFacade operationFacade, LocalDate startDate, LocalDate endDate, List<Category> categoryList) {
        this.operationFacade = operationFacade;
        this.startDate = startDate;
        this.endDate = endDate;
        this.categoryList = categoryList;
    }

    @Override
    public CategorySummaryDTO execute() {
        List<Operation> operations = operationFacade.findByDateBetween(startDate, endDate);

        Map<String, Category> categoryMap = categoryList.stream()
                .collect(Collectors.toMap(Category::getId, category -> category));

        List<CategoryStatistic> statistics = new ArrayList<>();

        Map<String, List<Operation>> operationsByCategory = operations.stream()
                .filter(op -> categoryMap.containsKey(op.getCategoryId()))
                .collect(Collectors.groupingBy(Operation::getCategoryId));

        BigDecimal totalIncome = calculateTotalByType(operations, TransactionType.INCOME);
        BigDecimal totalExpense = calculateTotalByType(operations, TransactionType.EXPENSE);

        for (Map.Entry<String, List<Operation>> entry : operationsByCategory.entrySet()) {
            String categoryId = entry.getKey();
            List<Operation> categoryOperations = entry.getValue();
            Category category = categoryMap.get(categoryId);

            BigDecimal categoryTotal = categoryOperations.stream()
                    .map(Operation::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            float percentage = calculatePercentage(categoryTotal, category.getType(), totalIncome, totalExpense);

            CategoryStatistic statistic = new CategoryStatistic(
                    category,
                    categoryTotal,
                    percentage,
                    category.getType()
            );

            statistics.add(statistic);
        }

        return new CategorySummaryDTO(startDate, endDate, statistics);
    }

    private BigDecimal calculateTotalByType(List<Operation> operations, TransactionType type) {
        return operations.stream()
                .filter(op -> op.getType() == type)
                .map(Operation::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private float calculatePercentage(BigDecimal categoryAmount, TransactionType type,
                                      BigDecimal totalIncome, BigDecimal totalExpense) {
        BigDecimal totalForType = type == TransactionType.INCOME ? totalIncome : totalExpense;

        if (totalForType.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0f;
        }

        return categoryAmount.divide(totalForType, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"))
                .floatValue();
    }
}
