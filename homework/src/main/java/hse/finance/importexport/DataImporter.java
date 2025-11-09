package hse.finance.importexport;

import hse.finance.domain.BankAccount;
import hse.finance.domain.Category;
import hse.finance.domain.Operation;
import hse.finance.dto.ImportStatistic;
import hse.finance.service.BankAccountFacade;
import hse.finance.service.CategoryFacade;
import hse.finance.service.OperationFacade;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class DataImporter {
    protected final BankAccountFacade accountFacade;
    protected final CategoryFacade categoryFacade;
    protected final OperationFacade operationFacade;

    protected final List<BankAccount> accountsToImport = new ArrayList<>();
    protected final List<Category> categoriesToImport = new ArrayList<>();
    protected final List<Operation> operationsToImport = new ArrayList<>();
    protected final List<String> errors = new ArrayList<>();

    public DataImporter(BankAccountFacade accountFacade,
                        CategoryFacade categoryFacade,
                        OperationFacade operationFacade) {
        this.accountFacade = accountFacade;
        this.categoryFacade = categoryFacade;
        this.operationFacade = operationFacade;
    }

    public final ImportStatistic importData(File file) {
        try {
            resetState();
            validateFile(file);
            String rawData = readFile(file);
            parseData(rawData);
            validateDataConsistency();

            if (errors.isEmpty()) {
                saveAllData();
            }

            return new ImportStatistic(
                    accountsToImport.size(),
                    categoriesToImport.size(),
                    operationsToImport.size(),
                    errors
            );

        } catch (Exception e) {
            errors.add("Критическая ошибка импорта: " + e.getMessage());
            return new ImportStatistic(0, 0, 0, errors);
        }
    }

    protected abstract void parseData(String rawData) throws Exception;

    private void resetState() {
        accountsToImport.clear();
        categoriesToImport.clear();
        operationsToImport.clear();
        errors.clear();
    }

    private void validateFile(File file) {
        if (!file.exists()) {
            throw new IllegalArgumentException("Файл не существует: " + file.getPath());
        }
        if (!file.canRead()) {
            throw new IllegalArgumentException("Нет доступа для чтения файла: " + file.getPath());
        }
    }

    private String readFile(File file) throws IOException {
        return Files.readString(file.toPath());
    }

    private void validateDataConsistency() {
        validateUniqueIds();
        validateReferences();
    }

    private void validateUniqueIds() {
        Set<String> accountIds = accountsToImport.stream()
                .map(BankAccount::getId)
                .collect(Collectors.toSet());
        if (accountIds.size() != accountsToImport.size()) {
            errors.add("Найдены дублирующиеся ID счетов");
        }

        Set<String> categoryIds = categoriesToImport.stream()
                .map(Category::getId)
                .collect(Collectors.toSet());
        if (categoryIds.size() != categoriesToImport.size()) {
            errors.add("Найдены дублирующиеся ID категорий");
        }

        Set<String> operationIds = operationsToImport.stream()
                .map(Operation::getId)
                .collect(Collectors.toSet());
        if (operationIds.size() != operationsToImport.size()) {
            errors.add("Найдены дублирующиеся ID операций");
        }
    }

    private void validateReferences() {
        Set<String> categoryIds = categoriesToImport.stream()
                .map(Category::getId)
                .collect(Collectors.toSet());

        Set<String> accountIds = accountsToImport.stream()
                .map(BankAccount::getId)
                .collect(Collectors.toSet());

        for (Operation operation : operationsToImport) {
            if (!categoryIds.contains(operation.getCategoryId())) {
                errors.add("Операция " + operation.getId() + " ссылается на несуществующую категорию: " + operation.getCategoryId());
            }
            if (!accountIds.contains(operation.getBankAccountId())) {
                errors.add("Операция " + operation.getId() + " ссылается на несуществующий счет: " + operation.getBankAccountId());
            }
        }
    }

    private void saveAllData() {
        saveCategories();
        saveAccounts();
        saveOperations();
    }

    private void saveCategories() {
        for (Category category : categoriesToImport) {
            try {
                Category created = categoryFacade.createCategory(category.getName(), category.getType());
                updateCategoryReferences(category.getId(), created.getId());
            } catch (Exception e) {
                errors.add("Ошибка создания категории '" + category.getName() + "': " + e.getMessage());
            }
        }
    }

    private void saveAccounts() {
        for (BankAccount account : accountsToImport) {
            try {
                BankAccount created = accountFacade.createAccount(account.getName(), account.getBalance());
                updateAccountReferences(account.getId(), created.getId());
            } catch (Exception e) {
                errors.add("Ошибка создания счета '" + account.getName() + "': " + e.getMessage());
            }
        }
    }

    private void saveOperations() {
        for (Operation operation : operationsToImport) {
            try {
                operationFacade.add(operation);
            } catch (Exception e) {
                errors.add("Ошибка создания операции: " + e.getMessage());
            }
        }
    }

    private void updateCategoryReferences(String oldCategoryId, String newCategoryId) {
        operationsToImport.stream()
                .filter(op -> op.getCategoryId().equals(oldCategoryId))
                .forEach(op -> op.setCategoryId(newCategoryId));
    }

    private void updateAccountReferences(String oldAccountId, String newAccountId) {
        operationsToImport.stream()
                .filter(op -> op.getBankAccountId().equals(oldAccountId))
                .forEach(op -> op.setBankAccountId(newAccountId));
    }
}