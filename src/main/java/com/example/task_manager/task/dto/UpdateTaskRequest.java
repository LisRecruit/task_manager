package com.example.task_manager.task.dto;

public record UpdateTaskRequest(Long taskId,
                                String taskDescription,
                                String responsiblePersonNote,
                                String directManagerNote,
                                Long responsiblePersonId,
                                String dueDate,
                                String taskType,
                                boolean taskComplete) {
}
