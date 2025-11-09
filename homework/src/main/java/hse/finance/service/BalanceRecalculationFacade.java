package hse.finance.service;

import hse.finance.command.RecalculateBalanceCommand;
import hse.finance.command.TimingCommandDecorator;
import hse.finance.domain.Operation;
import hse.finance.domain.TransactionType;
import hse.finance.dto.RecalculationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BalanceRecalculationFacade {
    private final BankAccountFacade accountFacade;
    private final OperationFacade operationFacade;

    @Autowired
    public BalanceRecalculationFacade(BankAccountFacade accountFacade,
                                      OperationFacade operationFacade) {
        this.accountFacade = accountFacade;
        this.operationFacade = operationFacade;
    }

    public RecalculationDTO recalculateBalancesWithTiming() {
        RecalculateBalanceCommand command = new RecalculateBalanceCommand(accountFacade, operationFacade);
        TimingCommandDecorator<RecalculationDTO> timedCommand =
                new TimingCommandDecorator<>(command);

        RecalculationDTO result = timedCommand.execute();

        System.out.printf("Пересчет балансов выполнен за %.2f мс\n",
                timedCommand.getExecutionTimeMs());

        return result;
    }

    public RecalculationDTO checkBalances() {
        RecalculateBalanceCommand command = new RecalculateBalanceCommand(accountFacade, operationFacade);
        return command.execute();
    }

    public BigDecimal verifyAccountBalance(String accountId) {
        List<Operation> operations = operationFacade.getOperationsByAccountId(accountId);

        return operations.stream()
                .map(op -> op.getType() == TransactionType.INCOME ?
                        op.getAmount() : op.getAmount().negate())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}