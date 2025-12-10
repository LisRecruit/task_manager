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
//    @Mapping(source = "dueDate", target = "dueDate", dateFormat = "yyyy-MM-dd")
    @Mapping(target = "dueDate",
            expression = "java(task.getDueDate() != null ? task.getDueDate().format(java.time.format.DateTimeFormatter.ofPattern(\"MM dd yyyy\")) : null)")
//    @Mapping(source = "completionDate", target = "completionDate", dateFormat = "yyyy-MM-dd")
    @Mapping(target = "completionDate",
            expression = "java(task.getCompletionDate() != null ? task.getCompletionDate().format(java.time.format.DateTimeFormatter.ofPattern(\"MM dd yyyy\")) : null)")
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

    @Mapping(target = "daysOverdue", expression = "java(calculateDaysOverdue(task))")

    TaskResponse toResponse (Task task);

    List<TaskResponse> toResponses(List<Task> tasks);

    default String formatPeriod(java.time.LocalDate period) {
        return period != null ? period.format(java.time.format.DateTimeFormatter.ofPattern("yyyy MMM", java.util.Locale.ENGLISH)) : null;
    }

    default Integer calculateDaysOverdue(Task task) {
        if (task.getTaskComplete() != null && task.getTaskComplete()) {
            return null; // если выполнена — null или 0 (на твой выбор)
        }
        if (task.getDueDate() == null) {
            return null;
        }
        return (int) java.time.temporal.ChronoUnit.DAYS.between(task.getDueDate(), java.time.LocalDate.now());
    }
}
