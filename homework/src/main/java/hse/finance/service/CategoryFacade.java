package hse.finance.service;

import hse.finance.domain.Category;
import hse.finance.domain.TransactionType;
import hse.finance.id_generation.IdGenerationContext;
import hse.finance.repository.proxy.CategoryRepositoryProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryFacade {
    private final CategoryRepositoryProxy categoryRepository;
    private final IdGenerationContext idContext;

    @Autowired
    public CategoryFacade(CategoryRepositoryProxy categoryRepository, IdGenerationContext idContext) {
        this.categoryRepository = categoryRepository;
        this.idContext = idContext;
    }

    public Category createCategory(String name, TransactionType type) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Название категории не может быть пустым");
        }

        Category category = Category.create(name, type, idContext);
        categoryRepository.add(category);
        return category;
    }

    public List<Category> getAllCategories() {
        return categoryRepository.getAll();
    }

    public Category getCategory(String id) {
        return categoryRepository.get(id);
    }

    public void remove(String id) {
        categoryRepository.remove(id);
    }

    public Category updateCategory(String categoryId, String newName, TransactionType newType) {
        Category category = categoryRepository.get(categoryId);
        category.setName(newName);
        category.setType(newType);
        categoryRepository.update(category);
        return category;
    }
}