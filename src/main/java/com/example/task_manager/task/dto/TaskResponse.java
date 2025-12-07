package com.example.task_manager.task.dto;


import com.example.task_manager.task.TaskType;

import java.util.List;

public record TaskResponse(Long id,
                           String taskDescription,
                           String responsiblePersonNote,
                           Long directManagerId,
                           String directManagerNote,
                           Long responsiblePersonId,
                           String responsiblePerson,
                           Long taskSetById,
                           String taskSetBy,
                           String dueDate,
                           String completionDate,
                           String period,
                           Integer daysOverdue,
                           List<TaskResponse> requiresTasksToComplete,
                           Boolean taskComplete,
                           TaskType taskType
                           ) {
}
