package com.example.task_manager.task;

import com.example.task_manager.auth.user.UserMapper;
import com.example.task_manager.task.dto.TaskResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface TaskMapper {
    @Mapping(source = "dueDate", target = "dueDate", dateFormat = "yyyy-MM-dd")
    @Mapping(source = "completionDate", target = "completionDate", dateFormat = "yyyy-MM-dd")
    @Mapping(
            target = "responsiblePerson",
            expression = "java(task.getResponsiblePerson() != null ? task.getResponsiblePerson().getUserName() : null)")
    @Mapping(
            target = "taskSetBy",
            expression = "java(task.getTaskSetBy() != null ? task.getTaskSetBy().getUserName() : null)")
    @Mapping(target = "requiresTasksToComplete", source = "subTasks")
    @Mapping(
            target = "taskSetById",
            expression = "java(task.getTaskSetBy() != null ? task.getTaskSetBy().getId() : null)")
    @Mapping(
            target = "responsiblePersonId",
            expression = "java(task.getResponsiblePerson() != null ? task.getResponsiblePerson().getId() : null)"
    )
    @Mapping(
            target = "directManagerId",
            expression = "java(task.getResponsiblePerson() != null && task.getResponsiblePerson().getDirectManager() != null ? task.getResponsiblePerson().getDirectManager().getId() : null)"
    )
    @Mapping(target = "period", expression = "java(formatPeriod(task.getPeriod()))")

    TaskResponse toResponse (Task task);

    List<TaskResponse> toResponses(List<Task> tasks);

    default String formatPeriod(java.time.LocalDate period) {
        return period != null ? period.format(java.time.format.DateTimeFormatter.ofPattern("yyyy MMM", java.util.Locale.ENGLISH)) : null;
    }
}
