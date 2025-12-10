package com.example.task_manager.task.dto;

public record UpdateTaskRequest(Long id,
                                String taskDescription,
                                String responsiblePersonNote,
                                String directManagerNote,
                                Long responsiblePersonId,
                                String dueDate,
                                String period,
                                String taskType,
                                boolean taskComplete) {
}
