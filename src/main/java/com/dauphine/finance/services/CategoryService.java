package com.dauphine.finance.services;

import com.dauphine.finance.exceptions.CategoryNotFoundException;
import com.dauphine.finance.models.Category;
import com.dauphine.finance.repositories.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getAll(String name) {
        if (name != null && !name.isBlank()) {
            return categoryRepository.findByNameContainingIgnoreCase(name);
        }
        return categoryRepository.findAll();
    }

    public Category getById(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
    }

    public Category create(Category category) {
        validateName(category.getName());
        return categoryRepository.save(category);
    }

    public Category update(UUID id, String name) {
        validateName(name);
        Category existing = getById(id);
        existing.setName(name);
        return categoryRepository.save(existing);
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Category name is required");
        }
    }

    public void delete(UUID id) {
        Category existing = getById(id);
        categoryRepository.delete(existing);
    }
}
