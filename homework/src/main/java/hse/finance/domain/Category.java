package hse.finance.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Data;
import hse.finance.id_generation.IdGenerationContext;
import hse.finance.importexport.ExportVisitor;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "categories")
@Data
@EqualsAndHashCode(callSuper = true)
public class Category extends BaseEntity {
    @Enumerated
    @Column(nullable = false)
    private TransactionType type;

    protected Category() {}

    public static Category create(String name, TransactionType type, IdGenerationContext context) {
        Category category = new Category();
        category.setId(context.generateId("category"));
        category.setName(name);
        category.setType(type);
        return category;
    }

    public static Category create(String name, TransactionType type, String id) {
        Category category = new Category();
        category.setId(id);
        category.setName(name);
        category.setType(type);
        return category;
    }

    boolean isIncome() {
        return type.isIncome();
    }
    boolean isExpense() {
        return type.isExpense();
    }

    public void accept(ExportVisitor visitor) {
        visitor.visit(this);
    }
}

