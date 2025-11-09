package hse.finance.command;

import hse.finance.domain.BankAccount;
import hse.finance.domain.Operation;
import hse.finance.domain.TransactionType;
import hse.finance.dto.RecalculationDTO;
import hse.finance.service.BankAccountFacade;
import hse.finance.service.OperationFacade;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecalculateBalanceCommand implements Command<RecalculationDTO> {
    private final BankAccountFacade accountFacade;
    private final OperationFacade operationFacade;

    private final Map<String, BigDecimal> originalBalances = new HashMap<>();
    private RecalculationDTO result;

    public RecalculateBalanceCommand(BankAccountFacade accountFacade,
                                     OperationFacade operationFacade) {
        this.accountFacade = accountFacade;
        this.operationFacade = operationFacade;
    }

    @Override
    public RecalculationDTO execute() {
        List<BankAccount> accounts = accountFacade.getAll();
        List<Operation> allOperations = operationFacade.getAllOperations();

        int checkedAccounts = 0;
        int correctedAccounts = 0;
        BigDecimal totalDiscrepancy = BigDecimal.ZERO;
        List<String> corrections = new ArrayList<>();

        for (BankAccount account : accounts) {
            originalBalances.put(account.getId(), account.getBalance());
        }

        for (BankAccount account : accounts) {
            checkedAccounts++;

            BigDecimal calculatedBalance = calculateBalanceFromOperations(account.getId(), allOperations);
            BigDecimal currentBalance = account.getBalance();

            BigDecimal discrepancy = calculatedBalance.subtract(currentBalance).abs();

            if (discrepancy.compareTo(BigDecimal.ZERO) > 0) {
                account.setBalance(calculatedBalance);

                correctedAccounts++;
                totalDiscrepancy = totalDiscrepancy.add(discrepancy);

                String correction = String.format(
                        "Счет '%s': было %.2f, стало %.2f (расхождение: %.2f)",
                        account.getName(), currentBalance, calculatedBalance, discrepancy
                );
                corrections.add(correction);
            }
        }

        this.result = new RecalculationDTO(checkedAccounts, correctedAccounts,
                corrections, totalDiscrepancy);
        return result;
    }

    public void undo() {
        if (originalBalances.isEmpty()) {
            throw new IllegalStateException("Нет данных для отката");
        }

        List<BankAccount> accounts = accountFacade.getAll();
        for (BankAccount account : accounts) {
            BigDecimal originalBalance = originalBalances.get(account.getId());
            if (originalBalance != null) {
                account.setBalance(originalBalance);
                accountFacade.updateAccount(account);
            }
        }

        originalBalances.clear();
    }


    private BigDecimal calculateBalanceFromOperations(String accountId, List<Operation> allOperations) {
        return allOperations.stream()
                .filter(op -> op.getBankAccountId().equals(accountId))
                .map(op -> {
                    if (op.getType() == TransactionType.INCOME) {
                        return op.getAmount();
                    } else {
                        return op.getAmount().negate();
                    }
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}