package hse.finance.repository.proxy;

import hse.finance.domain.Category;
import hse.finance.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryRepositoryProxy {
    private final CategoryRepository realRepository;
    private final List<Category> cache = new ArrayList<>();
    private boolean cacheLoaded = false;

    public CategoryRepositoryProxy(@Qualifier("categoryRepository") CategoryRepository realRepository) {
        this.realRepository = realRepository;
    }

    private void ensureCacheLoaded() {
        if (!cacheLoaded) {
            cache.clear();
            cache.addAll(realRepository.getAll());
            cacheLoaded = true;
            System.out.println("Кэш категорий загружен: " + cache.size() + " записей");
        }
    }
    public void add(Category category) {
        realRepository.add(category);
        cache.removeIf(cat -> cat.getId().equals(category.getId()));
        cache.add(category);
        System.out.println("Категория добавлена в кэш: " + category.getName());
    }

    public Category get(String id) {
        ensureCacheLoaded();
        return cache.stream()
                .filter(cat -> cat.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public void remove(String id) {
        realRepository.remove(id);
        cache.removeIf(cat -> cat.getId().equals(id));
        System.out.println("Категория удалена из кэша: " + id);
    }

    public void update(Category category) {
        realRepository.update(category.getId(), category);
        cache.removeIf(cat -> cat.getId().equals(category.getId()));
        cache.add(category);
        System.out.println("Категория обновлена в кэше: " + category.getName());
    }

    public ArrayList<Category> getAll() {
        ensureCacheLoaded();
        return new ArrayList<>(cache);
    }

    public void refreshCache() {
        cacheLoaded = false;
        ensureCacheLoaded();
        System.out.println("Кэш категорий обновлен");
    }

    public void clearCache() {
        cache.clear();
        cacheLoaded = false;
        System.out.println("Кэш категорий очищен");
    }
}