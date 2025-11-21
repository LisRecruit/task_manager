package com.example.task_manager.task.dto;

public record CompleteTaskResponse(
        TaskResponse completedTask,
        TaskResponse newTask
) {
}
