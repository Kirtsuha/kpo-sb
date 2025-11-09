package hse.finance.service;

import hse.finance.domain.Operation;
import hse.finance.domain.TransactionType;
import hse.finance.id_generation.IdGenerationContext;
import hse.finance.repository.proxy.OperationRepositoryProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class OperationFacade {
    private final OperationRepositoryProxy operationRepository;
    private final IdGenerationContext idContext;

    @Autowired
    public OperationFacade(OperationRepositoryProxy operationRepository, IdGenerationContext idContext) {
        this.operationRepository = operationRepository;
        this.idContext = idContext;
    }

    public Operation createOperation(TransactionType type, String categoryId, String bankAccountId,
                                     BigDecimal amount, LocalDate date, String description) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Сумма операции должна быть положительной");
        }
        if (date.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Дата операции не может быть в будущем");
        }

        Operation operation = Operation.create(type, categoryId, bankAccountId, amount, date, description, idContext);
        operationRepository.add(operation);
        return operation;
    }

    public void add(Operation operation) {
        operationRepository.add(operation);
    }

    public Operation get(String categoryId) {
        return operationRepository.get(categoryId);
    }

    public List<Operation> getAllOperations() {
        return operationRepository.getAll();
    }

    public List<Operation> getOperationsByAccountId(String accountId) {
        return operationRepository.findByAccountId(accountId);
    }

    public List<Operation> findByDateBetween(LocalDate startDate, LocalDate endDate) {
        return operationRepository.findByDateBetween(startDate, endDate);
    }

    public Operation updateOperation(String operationId,
                                     TransactionType newType,
                                     String newCategoryId,
                                     String newAccountId,
                                     BigDecimal newAmount,
                                     LocalDate newDate,
                                     String newDescription) {
        Operation operation = operationRepository.get(operationId);
        operation.setType(newType);
        operation.setCategoryId(newCategoryId);
        operation.setBankAccountId(newAccountId);
        operation.setAmount(newAmount);
        operation.setDate(newDate);
        operation.setDescription(newDescription);
        operationRepository.update(operationId, operation);
        return operation;
    }

    public void remove(String operationId) {
        operationRepository.remove(operationId);
    }
}