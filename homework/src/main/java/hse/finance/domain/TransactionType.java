package hse.finance.domain;

public enum TransactionType {
    INCOME("Доход"),
    EXPENSE("Расход");

    private final String description;

    TransactionType(String description) {
        this.description = description;
    }

    public boolean isIncome() {
        return this == INCOME;
    }
    public boolean isExpense() {
        return this == EXPENSE;
    }
    public String getDescription() {
        return description;
    }
}
