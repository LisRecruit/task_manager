package com.example.task_manager.task.dto;

public record DependencyStatusResponse(Long id,
                                       Boolean taskComplete,
                                       String taskDescription,
                                       Integer daysOverdue) {
}
