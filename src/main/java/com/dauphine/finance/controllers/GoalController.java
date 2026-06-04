package com.dauphine.finance.controllers;

import com.dauphine.finance.dto.GoalCreateDTO;
import com.dauphine.finance.dto.GoalUpdateDTO;
import com.dauphine.finance.models.Goal;
import com.dauphine.finance.services.GoalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Goals", description = "Manage savings goals")
@RestController
@RequestMapping("/v1/goals")
public class GoalController {
    private final GoalService goalService;

    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    @GetMapping
    @Operation(summary = "List goals")
    public ResponseEntity<List<Goal>> getAll() {
        return ResponseEntity.ok(goalService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get goal by id")
    public ResponseEntity<Goal> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(goalService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Create goal")
    public ResponseEntity<Goal> create(@RequestBody GoalCreateDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Goal payload is required");
        }
        Goal goal = new Goal();
        goal.setName(dto.getName());
        goal.setTargetAmount(dto.getTargetAmount());
        goal.setCurrentAmount(dto.getCurrentAmount());
        goal.setDeadline(dto.getDeadline());

        Goal created = goalService.create(goal);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update goal")
    public ResponseEntity<Goal> update(@PathVariable UUID id, @RequestBody GoalUpdateDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Goal payload is required");
        }
        Goal goal = new Goal();
        goal.setName(dto.getName());
        goal.setTargetAmount(dto.getTargetAmount());
        goal.setCurrentAmount(dto.getCurrentAmount());
        goal.setDeadline(dto.getDeadline());

        Goal updated = goalService.update(id, goal);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/amount")
    @Operation(summary = "Add amount to goal")
    public ResponseEntity<Goal> addAmount(@PathVariable UUID id, @RequestBody AddAmountRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Add amount payload is required");
        }
        Goal updated = goalService.addAmount(id, request.getAmount());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete goal")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        goalService.delete(id);
        return ResponseEntity.noContent().build();
    }

    public static class AddAmountRequest {
        private BigDecimal amount;

        public AddAmountRequest() {
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }
    }
}
