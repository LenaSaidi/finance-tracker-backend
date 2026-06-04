package com.dauphine.finance.controllers;

import com.dauphine.finance.dto.CategoryCreateDTO;
import com.dauphine.finance.models.Category;
import com.dauphine.finance.models.Transaction;
import com.dauphine.finance.services.CategoryService;
import com.dauphine.finance.services.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Categories", description = "Manage transaction categories")
@RestController
@RequestMapping("/v1/categories")
public class CategoryController {
    private final CategoryService categoryService;
    private final TransactionService transactionService;

    public CategoryController(CategoryService categoryService, TransactionService transactionService) {
        this.categoryService = categoryService;
        this.transactionService = transactionService;
    }

    @GetMapping
    @Operation(summary = "List categories", description = "Optional name filter with query param.")
    public ResponseEntity<List<Category>> getAll(@RequestParam(required = false) String name) {
        return ResponseEntity.ok(categoryService.getAll(name));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by id")
    public ResponseEntity<Category> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(categoryService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Create category")
    public ResponseEntity<Category> create(@RequestBody CategoryCreateDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Category payload is required");
        }
        Category category = new Category(dto.getName());
        Category created = categoryService.create(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update category")
    public ResponseEntity<Category> update(@PathVariable UUID id, @RequestBody CategoryCreateDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Category payload is required");
        }
        Category updated = categoryService.update(id, dto.getName());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete category")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{categoryId}/transactions")
    @Operation(summary = "List transactions for category")
    public ResponseEntity<List<Transaction>> getTransactionsByCategory(@PathVariable UUID categoryId) {
        return ResponseEntity.ok(transactionService.getByCategoryId(categoryId));
    }
}
