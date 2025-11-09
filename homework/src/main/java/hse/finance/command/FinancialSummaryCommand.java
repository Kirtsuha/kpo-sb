package hse.finance.command;

import hse.finance.domain.Operation;
import hse.finance.domain.TransactionType;
import hse.finance.dto.FinancialSummaryDTO;
import hse.finance.service.OperationFacade;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class FinancialSummaryCommand implements Command<FinancialSummaryDTO> {
    private final OperationFacade operationFacade;
    private final LocalDate startDate;
    private final LocalDate endDate;
    public FinancialSummaryCommand(OperationFacade operationFacade, LocalDate startDate, LocalDate endDate) {
        this.operationFacade = operationFacade;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public FinancialSummaryDTO execute() {
        List<Operation> operations = operationFacade.findByDateBetween(startDate, endDate);

        BigDecimal totalIncome = operations.stream()
                .filter(op -> op.getType() == TransactionType.INCOME)
                .map(Operation::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpense = operations.stream()
                .filter(op -> op.getType() == TransactionType.EXPENSE)
                .map(Operation::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new FinancialSummaryDTO(startDate, endDate, totalIncome, totalExpense, totalIncome.subtract(totalExpense));
    }
}
