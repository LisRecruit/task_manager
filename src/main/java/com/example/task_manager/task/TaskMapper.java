package com.example.task_manager.task;

import com.example.task_manager.task.dto.TaskResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TaskMapper {
    TaskResponse toResponse (Task task);
}
