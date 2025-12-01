package com.example.task_manager.task;

import com.example.task_manager.auth.user.UserMapper;
import com.example.task_manager.task.dto.TaskResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface TaskMapper {
    @Mapping(source = "dueDate", target = "dueDate", dateFormat = "yyyy-MM-dd")
    @Mapping(source = "completionDate", target = "completionDate", dateFormat = "yyyy-MM-dd")
    @Mapping(target = "responsiblePerson", source = "responsiblePerson")
    @Mapping(target = "taskSetBy", source = "taskSetBy")
    @Mapping(target = "requiresTasksToComplete", source = "subTasks")
    TaskResponse toResponse (Task task);

    List<TaskResponse> toResponses(List<Task> tasks);
}
