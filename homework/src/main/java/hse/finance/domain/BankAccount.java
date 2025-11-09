package hse.finance.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import hse.finance.id_generation.IdGenerationContext;
import hse.finance.importexport.ExportVisitor;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Entity
@Table(name = "bank_accounts")
@Data
@EqualsAndHashCode(callSuper = true)
public class BankAccount extends BaseEntity {
    @Column(nullable = false)
    private BigDecimal balance;

    protected BankAccount() {}

    public static BankAccount create(String name, BigDecimal balance, IdGenerationContext context) {
        BankAccount bankAccount = new BankAccount();
        bankAccount.setId(context.generateId("bankaccount"));
        bankAccount.setName(name);
        bankAccount.setBalance(balance);
        return bankAccount;
    }

    public static BankAccount create(String name, BigDecimal balance, String id) {
        BankAccount bankAccount = new BankAccount();
        bankAccount.setId(id);
        bankAccount.setName(name);
        bankAccount.setBalance(balance);
        return bankAccount;
    }

    public void accept(ExportVisitor visitor) {
        visitor.visit(this);
    }
}
