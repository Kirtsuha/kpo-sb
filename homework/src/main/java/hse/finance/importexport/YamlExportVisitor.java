package hse.finance.importexport;

import hse.finance.domain.BankAccount;
import hse.finance.domain.Category;
import hse.finance.domain.Operation;
import org.yaml.snakeyaml.Yaml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YamlExportVisitor implements ExportVisitor {
    private final Yaml yaml = new Yaml();
    private final Map<String, Object> result = new HashMap<>();
    private final List<Map<String, Object>> accounts = new ArrayList<>();
    private final List<Map<String, Object>> categories = new ArrayList<>();
    private final List<Map<String, Object>> operations = new ArrayList<>();

    public YamlExportVisitor() {
        reset();
    }

    @Override
    public void visit(BankAccount account) {
        Map<String, Object> accountMap = new HashMap<>();
        accountMap.put("id", account.getId());
        accountMap.put("name", account.getName());
        accountMap.put("balance", account.getBalance());
        accounts.add(accountMap);
    }

    @Override
    public void visit(Category category) {
        Map<String, Object> categoryMap = new HashMap<>();
        categoryMap.put("id", category.getId());
        categoryMap.put("name", category.getName());
        categoryMap.put("type", category.getType().name());
        categories.add(categoryMap);
    }

    @Override
    public void visit(Operation operation) {
        Map<String, Object> operationMap = new HashMap<>();
        operationMap.put("id", operation.getId());
        operationMap.put("type", operation.getType().name());
        operationMap.put("bankAccountId", operation.getBankAccountId());
        operationMap.put("categoryId", operation.getCategoryId());
        operationMap.put("amount", operation.getAmount());
        operationMap.put("date", operation.getDate().toString());
        operationMap.put("description", operation.getDescription());
        operations.add(operationMap);
    }

    @Override
    public String getResult() {
        result.put("accounts", accounts);
        result.put("categories", categories);
        result.put("operations", operations);

        return yaml.dumpAsMap(result);
    }

    @Override
    public String getFormat() {
        return "yaml";
    }

    @Override
    public void reset() {
        accounts.clear();
        categories.clear();
        operations.clear();
        result.clear();
        result.put("accounts", accounts);
        result.put("categories", categories);
        result.put("operations", operations);
    }
}