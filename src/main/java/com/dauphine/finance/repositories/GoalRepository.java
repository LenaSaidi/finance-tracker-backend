package com.dauphine.finance.repositories;

import com.dauphine.finance.models.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface GoalRepository extends JpaRepository<Goal, UUID> {
    @Query("SELECT g FROM Goal g ORDER BY g.createdDate DESC")
    List<Goal> findAllByOrderByCreatedDateDesc();
}

