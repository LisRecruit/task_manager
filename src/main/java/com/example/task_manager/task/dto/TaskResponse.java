package com.example.task_manager.task.dto;


import com.example.task_manager.auth.user.dto.response.UserResponse;
import com.example.task_manager.task.TaskType;

import java.util.List;

public record TaskResponse(String taskDescription,
                           String responsiblePersonNote,
                           String directManagerNote,
                           UserResponse responsiblePerson,
                           UserResponse taskSetBy,
                           String dueDate,
                           String completionDate,
                           Integer daysOverdue,
                           List<TaskResponse> requiresTasksToComplete,
                           Boolean taskComplete,
                           TaskType taskType) {
}
