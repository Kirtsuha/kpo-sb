package hse.finance.importexport;

import hse.finance.domain.BankAccount;
import hse.finance.domain.Category;
import hse.finance.domain.Operation;

public class CsvExportVisitor implements ExportVisitor {
    private final StringBuilder result = new StringBuilder();

    public CsvExportVisitor() {
        reset();
    }

    @Override
    public void visit(BankAccount account) {
        String line = String.format("ACCOUNT,%s,%s,%s\n",
                escapeCsv(account.getId()),
                escapeCsv(account.getName()),
                escapeCsv(account.getBalance().toString())
        );
        result.append(line);
    }

    @Override
    public void visit(Category category) {
        String line = String.format("CATEGORY,%s,%s,%s\n",
                escapeCsv(category.getId()),
                escapeCsv(category.getName()),
                escapeCsv(category.getType().name()));
        result.append(line);
    }

    @Override
    public void visit(Operation operation) {
        String line = String.format("OPERATION,%s,%s,%s,%s,%s,%s,%s\n",
                escapeCsv(operation.getId()),
                escapeCsv(operation.getType().name()),
                escapeCsv(operation.getBankAccountId()),
                escapeCsv(operation.getCategoryId()),
                escapeCsv(operation.getAmount().toString()),
                escapeCsv(operation.getDate().toString()),
                escapeCsv(operation.getDescription()));
        result.append(line);
    }

    @Override
    public String getResult() {
        return result.toString();
    }

    @Override
    public String getFormat() {
        return "csv";
    }

    @Override
    public void reset() {
        result.setLength(0);
        result.append("ENTITY_TYPE,ID,NAME,BALANCE,TYPE,BANK_ACCOUNT_ID,CATEGORY_ID,AMOUNT,DATE,DESCRIPTION\n");
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}