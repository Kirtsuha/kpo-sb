package hse.finance.repository;

import hse.finance.domain.Category;
import hse.finance.domain.TransactionType;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CategoryRepository {
    private final Map<String, Category> сategoryMap = new HashMap<>();
    public void add(Category category) {
        сategoryMap.put(category.getId(), category);
    }

    public Category get(String id) {
        return сategoryMap.get(id);
    }

    public void remove(String id) {
        сategoryMap.remove(id);
    }

    public void update(String id, Category newCategory) {
        сategoryMap.put(id, newCategory);
    }

    public ArrayList<Category> getAll() {
        return new ArrayList<>(сategoryMap.values());
    }

    public Optional<Category> findById(String id) {
        return Optional.ofNullable(сategoryMap.get(id));
    }

    public List<Category> findByType(TransactionType type) {
        return сategoryMap.values().stream()
                .filter(category -> category.getType() == type)
                .collect(Collectors.toList());
    }
}
