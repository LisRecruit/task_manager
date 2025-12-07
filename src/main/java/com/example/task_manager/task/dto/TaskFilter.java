package com.example.task_manager.task.dto;

import com.example.task_manager.task.TaskType;

import java.time.LocalDate;

public record TaskFilter(Boolean taskComplete,
                         LocalDate dueDateFrom,
                         LocalDate dueDateTo,
                         String taskType) {
}
