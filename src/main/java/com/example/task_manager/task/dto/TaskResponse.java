package com.example.task_manager.task.dto;

import com.example.task_manager.auth.user.User;
import com.example.task_manager.task.Task;
import com.example.task_manager.task.TaskType;

import java.util.List;

public record TaskResponse(String taskDescription,
                           String responsiblePersonNote,
                           String directManagerNote,
                           User responsiblePerson,
                           User taskSetBy,
                           String dueDate,
                           String completionDate,
                           Integer daysOverdue,
                           List<Task> requiresTasksToComplete,
                           Boolean taskComplete,
                           TaskType taskType) {
}
