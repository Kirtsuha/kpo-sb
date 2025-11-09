package hse.finance.dto;

import java.util.ArrayList;
import java.util.List;

public record ImportStatistic(int accounts, int categories, int operations, List<String> errors) {
    public ImportStatistic(int accounts, int categories, int operations, List<String> errors) {
        this.accounts = accounts;
        this.categories = categories;
        this.operations = operations;
        this.errors = errors != null ? errors : new ArrayList<>();
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    @Override
    public String toString() {
        return String.format("Импортировано: счетов=%d, категорий=%d, операций=%d. Ошибки: %s",
                accounts, categories, operations, errors);
    }
}