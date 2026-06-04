package com.dauphine.finance.services;

import com.dauphine.finance.exceptions.GoalNotFoundException;
import com.dauphine.finance.models.Goal;
import com.dauphine.finance.repositories.GoalRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class GoalService {
    private final GoalRepository goalRepository;

    public GoalService(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;
    }

    public List<Goal> getAll() {
        return goalRepository.findAllByOrderByCreatedDateDesc();
    }

    public Goal getById(UUID id) {
        return goalRepository.findById(id)
                .orElseThrow(() -> new GoalNotFoundException(id));
    }

    public Goal create(Goal goal) {
        validateGoal(goal);
        if (goal.getCurrentAmount() == null) {
            goal.setCurrentAmount(BigDecimal.ZERO);
        }
        return goalRepository.save(goal);
    }

    public Goal update(UUID id, Goal updated) {
        validateGoal(updated);
        Goal existing = getById(id);
        existing.setName(updated.getName());
        existing.setTargetAmount(updated.getTargetAmount());
        existing.setCurrentAmount(updated.getCurrentAmount());
        existing.setDeadline(updated.getDeadline());
        return goalRepository.save(existing);
    }

    private void validateGoal(Goal goal) {
        if (goal.getName() == null || goal.getName().isBlank()) {
            throw new IllegalArgumentException("Goal name is required");
        }
        if (goal.getTargetAmount() == null) {
            throw new IllegalArgumentException("Target amount is required");
        }
    }

    public Goal addAmount(UUID id, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        Goal goal = getById(id);
        goal.setCurrentAmount(goal.getCurrentAmount().add(amount));
        return goalRepository.save(goal);
    }

    public void delete(UUID id) {
        Goal existing = getById(id);
        goalRepository.delete(existing);
    }
}
