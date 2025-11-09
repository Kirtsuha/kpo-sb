package hse.finance.domain;

import jakarta.persistence.*;
import lombok.Data;
import hse.finance.id_generation.IdGenerationContext;
import hse.finance.importexport.ExportVisitor;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "operations")
@Data
@EqualsAndHashCode(callSuper = true)
public class Operation extends BaseEntity {
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Column(name = "bank_account_id", nullable = false)
    private String bankAccountId;

    @Column(name = "category_id", nullable = false)
    private String categoryId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDate date;

    private String description;
    static public Operation create(TransactionType type,
                                   String categoryId,
                                   String bankAccountId,
                                   BigDecimal amount,
                                   LocalDate date,
                                   String description,
                                   IdGenerationContext context) {
        Operation operation = new Operation();
        operation.setId(context.generateId("operation"));
        operation.setType(type);
        operation.setBankAccountId(bankAccountId);
        operation.setCategoryId(categoryId);
        operation.setAmount(amount);
        operation.setDate(date);
        operation.setDescription(description);
        return operation;
    }

    static public Operation create(TransactionType type,
                                   String categoryId,
                                   String bankAccountId,
                                   BigDecimal amount,
                                   LocalDate date,
                                   String description,
                                   String id) {
        Operation operation = new Operation();
        operation.setId(id);
        operation.setType(type);
        operation.setBankAccountId(bankAccountId);
        operation.setCategoryId(categoryId);
        operation.setAmount(amount);
        operation.setDate(date);
        operation.setDescription(description);
        return operation;
    }

    public void accept(ExportVisitor visitor) {
        visitor.visit(this);
    }
}
