package com.example.task_manager.task.dto;

public record CreateTaskRequest(String taskDescription,
                                Long responsiblePersonId,
//                                User taskSetBy, //get from token
                                String dueDate,
                                String taskType) {
}
