package com.example.task_manager.task;

import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;


public class TaskSpecifications {
    public static Specification<Task> taskComplete(Boolean taskComplete) {
        return (root, query, cb) -> {
            if (taskComplete == null) return null;
            return cb.equal(root.get("taskComplete"), taskComplete);
        };
    }

    public static Specification<Task> taskType(String taskType) {
        return (root, query, cb) -> {
            if (taskType == null || taskType.isBlank() || taskType.equals("")) return null;
            return cb.equal(root.get("taskType"), TaskType.valueOf(taskType.toUpperCase()));
        };
    }

    public static Specification<Task> dueDateBetween(LocalDate from, LocalDate to) {
        return (root, query, cb) -> {
            if (from == null && to == null) return null;
            if (from != null && to != null) return cb.between(root.get("dueDate"), from, to);
            if (from != null) return cb.greaterThanOrEqualTo(root.get("dueDate"), from);
            return cb.lessThanOrEqualTo(root.get("dueDate"), to);
        };
    }
}
