package hse.finance.service;

import hse.finance.command.CategorySummaryCommand;
import hse.finance.command.FinancialSummaryCommand;
import hse.finance.command.TimingCommandDecorator;
import hse.finance.domain.Category;
import hse.finance.dto.CategorySummaryDTO;
import hse.finance.dto.FinancialSummaryDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AnalyticsFacade {
    private final OperationFacade operationFacade;
    private final CategoryFacade categoryFacade;

    public AnalyticsFacade(OperationFacade operationFacade, CategoryFacade categoryFacade) {
        this.operationFacade = operationFacade;
        this.categoryFacade = categoryFacade;
    }

    public FinancialSummaryDTO getFinancialSummary(LocalDate startDate, LocalDate endDate) {
        FinancialSummaryCommand command = new FinancialSummaryCommand(
                operationFacade, startDate, endDate
        );

        TimingCommandDecorator<FinancialSummaryDTO> timedCommand =
                new TimingCommandDecorator<>(command);

        FinancialSummaryDTO result = timedCommand.execute();

        System.out.printf("Financial summary calculated in %.2f ms%n",
                timedCommand.getExecutionTimeMs());

        return result;
    }

    public CategorySummaryDTO getCategorySummary(LocalDate startDate, LocalDate endDate) {
        List<Category> allCategories = categoryFacade.getAllCategories();

        CategorySummaryCommand command = new CategorySummaryCommand(
                operationFacade, startDate, endDate, allCategories
        );

        TimingCommandDecorator<CategorySummaryDTO> timedCommand =
                new TimingCommandDecorator<>(command);

        CategorySummaryDTO result = timedCommand.execute();

        System.out.printf("Category summary calculated in %.2f ms%n",
                timedCommand.getExecutionTimeMs());

        return result;
    }
}
