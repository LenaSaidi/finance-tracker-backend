package com.dauphine.finance.exceptions;

import java.util.UUID;

public class GoalNotFoundException extends RuntimeException {
    public GoalNotFoundException(UUID id) {
        super("Goal not found: " + id);
    }
}

