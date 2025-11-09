package hse.finance.repository;

import hse.finance.domain.Operation;
import hse.finance.domain.TransactionType;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OperationRepository {
    private final Map<String, Operation> operationMap = new HashMap<>();
    public void add(Operation operation) {
        operationMap.put(operation.getId(), operation);
    }

    public Operation get(String id) {
        return operationMap.get(id);
    }

    public void remove(String id) {
        operationMap.remove(id);
    }

    public void update(String id, Operation newOperation) {
        operationMap.put(id, newOperation);
    }

    public ArrayList<Operation> getAll() {
        return new ArrayList<>(operationMap.values());
    }

    public List<Operation> findByDateBetween(LocalDate start, LocalDate end) {
        return operationMap.values().stream()
                .filter(op -> !op.getDate().isBefore(start) && !op.getDate().isAfter(end))
                .collect(Collectors.toList());
    }

    public List<Operation> findByType(TransactionType type) {
        return operationMap.values().stream()
                .filter(op -> op.getType() == type)
                .collect(Collectors.toList());
    }

    public List<Operation> findByAccountId(String accountId) {
        return operationMap.values().stream()
                .filter(op -> op.getBankAccountId().equals(accountId))
                .collect(Collectors.toList());
    }

    public List<Operation> findByCategoryId(String categoryId) {
        return operationMap.values().stream()
                .filter(op -> op.getCategoryId().equals(categoryId))
                .collect(Collectors.toList());
    }

    public List<Operation> findByDateBetweenAndType(LocalDate start, LocalDate end, TransactionType type) {
        return operationMap.values().stream()
                .filter(op -> !op.getDate().isBefore(start) && !op.getDate().isAfter(end))
                .filter(op -> op.getType() == type)
                .collect(Collectors.toList());
    }

    public List<Operation> findByAccountIdAndDateBetween(String accountId, LocalDate start, LocalDate end) {
        return operationMap.values().stream()
                .filter(op -> op.getBankAccountId().equals(accountId))
                .filter(op -> !op.getDate().isBefore(start) && !op.getDate().isAfter(end))
                .collect(Collectors.toList());
    }
}
