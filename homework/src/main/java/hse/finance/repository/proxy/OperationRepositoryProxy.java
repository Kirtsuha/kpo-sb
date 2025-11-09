package hse.finance.repository.proxy;

import hse.finance.domain.Operation;
import hse.finance.repository.OperationRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OperationRepositoryProxy {
    private final OperationRepository realRepository;
    private final List<Operation> cache = new ArrayList<>();
    private boolean cacheLoaded = false;

    public OperationRepositoryProxy(@Qualifier("operationRepository") OperationRepository realRepository) {
        this.realRepository = realRepository;
    }

    private void ensureCacheLoaded() {
        if (!cacheLoaded) {
            cache.clear();
            cache.addAll(realRepository.getAll());
            cacheLoaded = true;
            System.out.println("Кэш операций загружен: " + cache.size() + " записей");
        }
    }
    public void add(Operation operation) {
        realRepository.add(operation);
        cache.removeIf(op -> op.getId().equals(operation.getId()));
        cache.add(operation);
        System.out.println("Операция добавлена в кэш: " + operation.getDescription());
    }

    public Operation get(String id) {
        ensureCacheLoaded();
        return cache.stream()
                .filter(op -> op.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public void remove(String id) {
        realRepository.remove(id);
        cache.removeIf(op -> op.getId().equals(id));
        System.out.println("Операция удалена из кэша: " + id);
    }

    public void update(String id, Operation newOperation) {
        realRepository.update(id, newOperation);
        cache.removeIf(op -> op.getId().equals(id));
        cache.add(newOperation);
        System.out.println("Операция обновлена в кэше: " + newOperation.getDescription());
    }

    public ArrayList<Operation> getAll() {
        ensureCacheLoaded();
        return new ArrayList<>(cache);
    }

    public List<Operation> findByDateBetween(LocalDate start, LocalDate end) {
        ensureCacheLoaded();
        return cache.stream()
                .filter(op -> !op.getDate().isBefore(start) && !op.getDate().isAfter(end))
                .collect(Collectors.toList());
    }

    public List<Operation> findByAccountId(String accountId) {
        ensureCacheLoaded();
        return cache.stream()
                .filter(op -> op.getBankAccountId().equals(accountId))
                .collect(Collectors.toList());
    }

    public void refreshCache() {
        cacheLoaded = false;
        ensureCacheLoaded();
        System.out.println("Кэш операций обновлен");
    }

    public void clearCache() {
        cache.clear();
        cacheLoaded = false;
        System.out.println("Кэш операций очищен");
    }
}