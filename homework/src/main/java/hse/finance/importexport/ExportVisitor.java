package hse.finance.importexport;

import hse.finance.domain.BankAccount;
import hse.finance.domain.Category;
import hse.finance.domain.Operation;

public interface ExportVisitor {
    void visit(BankAccount account);
    void visit(Category category);
    void visit(Operation operation);

    String getResult();
    String getFormat();
    void reset();
}