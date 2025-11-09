package hse.finance.importexport;

import hse.finance.domain.BankAccount;
import hse.finance.domain.Category;
import hse.finance.domain.Operation;
import hse.finance.domain.TransactionType;
import hse.finance.service.BankAccountFacade;
import hse.finance.service.CategoryFacade;
import hse.finance.service.OperationFacade;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CsvDataImporter extends DataImporter {
    private final Map<String, String> accountNameToId = new HashMap<>();
    private final Map<String, String> categoryNameToId = new HashMap<>();

    public CsvDataImporter(BankAccountFacade accountFacade,
                           CategoryFacade categoryFacade,
                           OperationFacade operationFacade) {
        super(accountFacade, categoryFacade, operationFacade);
    }

    @Override
    protected void parseData(String rawData) throws Exception {
        String[] lines = rawData.split("\n");

        for (String line : lines) {
            if (line.trim().isEmpty()) continue;

            String[] parts = line.split(",");
            if (parts.length < 2) continue;

            String entityType = parts[0].trim();

            if ("ACCOUNT".equals(entityType)) {
                createAccountViaFacade(parts);
            } else if ("CATEGORY".equals(entityType)) {
                createCategoryViaFacade(parts);
            }
        }

        for (String line : lines) {
            if (line.trim().isEmpty()) continue;

            String[] parts = line.split(",");
            if (parts.length < 2) continue;

            String entityType = parts[0].trim();

            if ("OPERATION".equals(entityType)) {
                createOperationViaFacade(parts);
            }
        }
    }

    private void createAccountViaFacade(String[] parts) {
        try {
            if (parts.length < 4) return;

            String id = parts[1].trim();
            String name = parts[2].trim();
            BigDecimal balance = new BigDecimal(parts[3].trim());

            BankAccount account = BankAccount.create(name, balance, id);

            accountNameToId.put(name, id);
            accountsToImport.add(account);

        } catch (Exception e) {
            errors.add("Ошибка создания счета '" + parts[2] + "': " + e.getMessage());
        }
    }

    private void createCategoryViaFacade(String[] parts) {
        try {
            if (parts.length < 4) return;

            String id = parts[1].trim();
            String name = parts[2].trim();
            TransactionType type = TransactionType.valueOf(parts[3].trim().toUpperCase());

            Category category = Category.create(name, type, id);

            categoryNameToId.put(name, id);
            categoriesToImport.add(category);

        } catch (Exception e) {
            errors.add("Ошибка создания категории '" + parts[2] + "': " + e.getMessage());
        }
    }

    private void createOperationViaFacade(String[] parts) {
        try {
            if (parts.length < 8) return;

            String accountId = parts[3].trim();
            String categoryId = parts[4].trim();
            TransactionType type = TransactionType.valueOf(parts[2].trim().toUpperCase());
            BigDecimal amount = new BigDecimal(parts[5].trim());
            LocalDate date = LocalDate.parse(parts[6].trim());
            String description = parts[7].trim();

            if (accountId == null) {
                errors.add("Операция ссылается на несуществующий счет");
                return;
            }
            if (categoryId == null) {
                errors.add("Операция ссылается на несуществующую категорию" );
                return;
            }

            Operation operation = operationFacade.createOperation(type, categoryId, accountId, amount, date, description);

            operationsToImport.add(operation);

        } catch (Exception e) {
            errors.add("Ошибка создания операции: " + Arrays.toString(parts) + " - " + e.getMessage());
        }
    }
}