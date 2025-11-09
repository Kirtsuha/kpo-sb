package hse.finance.importexport;

import hse.finance.domain.BankAccount;
import hse.finance.domain.Category;
import hse.finance.domain.Operation;
import hse.finance.domain.TransactionType;
import hse.finance.service.BankAccountFacade;
import hse.finance.service.CategoryFacade;
import hse.finance.service.OperationFacade;
import org.yaml.snakeyaml.Yaml;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YamlDataImporter extends DataImporter {
    private final Yaml yaml = new Yaml();
    private final Map<String, String> accountNameToId = new HashMap<>();
    private final Map<String, String> categoryNameToId = new HashMap<>();

    public YamlDataImporter(BankAccountFacade accountFacade,
                            CategoryFacade categoryFacade,
                            OperationFacade operationFacade) {
        super(accountFacade, categoryFacade, operationFacade);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void parseData(String rawData) throws Exception {
        Map<String, Object> data = yaml.load(rawData);

        if (data.containsKey("accounts")) {
            List<Map<String, Object>> accountsList = (List<Map<String, Object>>) data.get("accounts");
            for (Map<String, Object> accountMap : accountsList) {
                createAccountViaFacade(accountMap);
            }
        }

        if (data.containsKey("categories")) {
            List<Map<String, Object>> categoriesList = (List<Map<String, Object>>) data.get("categories");
            for (Map<String, Object> categoryMap : categoriesList) {
                createCategoryViaFacade(categoryMap);
            }
        }

        if (data.containsKey("operations")) {
            List<Map<String, Object>> operationsList = (List<Map<String, Object>>) data.get("operations");
            for (Map<String, Object> operationMap : operationsList) {
                createOperationViaFacade(operationMap);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void createAccountViaFacade(Map<String, Object> accountMap) {
        try {
            String name = (String) accountMap.get("name");

            Object balance = accountMap.get("balance");
            BigDecimal balanceValue;
            if (balance instanceof Integer) {
                balanceValue = new BigDecimal((Integer) balance);
            } else if (balance instanceof Double) {
                balanceValue = new BigDecimal((Double) balance);
            } else {
                balanceValue = new BigDecimal(balance.toString());
            }

            String accountId = (String) accountMap.get("id");
            BankAccount account = BankAccount.create(name, balanceValue, accountId);

            accountNameToId.put(name, accountId);
            accountsToImport.add(account);

        } catch (Exception e) {
            errors.add("Ошибка создания счета: " + accountMap + " - " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void createCategoryViaFacade(Map<String, Object> categoryMap) {
        try {
            String name = (String) categoryMap.get("name");
            TransactionType type = TransactionType.valueOf(((String) categoryMap.get("type")).toUpperCase());

            String categoryId = (String) categoryMap.get("id");
            Category category = Category.create(name, type, categoryId);

            categoryNameToId.put(name, category.getId());
            categoriesToImport.add(category);

        } catch (Exception e) {
            errors.add("Ошибка создания категории: " + categoryMap + " - " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void createOperationViaFacade(Map<String, Object> operationMap) {
        try {
            String accountId = (String) operationMap.get("bankAccountId");
            String categoryId = (String) operationMap.get("categoryId");
            TransactionType type = TransactionType.valueOf(((String) operationMap.get("type")).toUpperCase());

            if (accountId == null) {
                errors.add("Операция ссылается на несуществующий счет");
                return;
            }
            if (categoryId == null) {
                errors.add("Операция ссылается на несуществующую категорию" );
                return;
            }

            Object amount = operationMap.get("amount");
            BigDecimal amountValue;
            if (amount instanceof Integer) {
                amountValue = new BigDecimal((Integer) amount);
            } else if (amount instanceof Double) {
                amountValue = new BigDecimal((Double) amount);
            } else {
                amountValue = new BigDecimal(amount.toString());
            }

            LocalDate date = LocalDate.parse((String) operationMap.get("date"));
            String description = (String) operationMap.get("description");

            String operationId = (String) operationMap.get("id");
            Operation operation = Operation.create(type, categoryId, accountId, amountValue, date, description, operationId);
            operationsToImport.add(operation);

        } catch (Exception e) {
            errors.add("Ошибка создания операции: " + operationMap + " - " + e.getMessage());
        }
    }
}