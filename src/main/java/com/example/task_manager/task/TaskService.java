package com.example.task_manager.task;

import com.example.task_manager.auth.user.User;
import com.example.task_manager.auth.user.UserService;
import com.example.task_manager.task.dto.CreateTaskRequest;
import com.example.task_manager.task.dto.TaskResponse;
import com.example.task_manager.task.dto.UpdateTaskRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserService userService;
    private final TaskMapper taskMapper;

    //get userCreatorId from token
    @Transactional
    public TaskResponse createTask (CreateTaskRequest request, Long userCreatorId) {

        User taskSetBy = userService.getUserById(userCreatorId);
        User responsiblePerson = userService.getUserById(request.responsiblePersonId());
        LocalDate dueDate = LocalDate.parse(request.dueDate());
        Integer daysOverdue = (int) ChronoUnit.DAYS.between(dueDate, LocalDate.now());
        //validate request.dueDate
        //validate request.taskType
        Task task = Task.builder()
                .taskDescription(request.taskDescription())
                .responsiblePerson(responsiblePerson)
                .taskSetBy(taskSetBy)
                .dueDate(dueDate)
                .daysOverdue(daysOverdue)
                .taskComplete(false)
                .taskType(TaskType.valueOf(request.taskType().toUpperCase()))
                .build();
        return taskMapper.toResponse(taskRepository.save(task));
    }

    @Transactional
    public TaskResponse updateTaskByManager (UpdateTaskRequest request, Long requesterId) {
        User requester = userService.getUserById(requesterId);
        Task task = taskRepository.findById(request.taskId())
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));
        Integer daysOverdue = (int) ChronoUnit.DAYS.between(task.getDueDate(), LocalDate.now());
        if (requester.getId().equals(task.getResponsiblePerson().getId())){
            task.setResponsiblePersonNote(request.responsiblePersonNote());
            task.setTaskComplete(request.taskComplete());
            task.setDaysOverdue(daysOverdue);
        } else if (requester.getId().equals(task.getTaskSetBy().getId())) {
            task.setTaskDescription(request.taskDescription());
            task.setDirectManagerNote(request.directManagerNote());
            User responsiblePerson = userService.getUserById(request.responsiblePersonId());
            task.setResponsiblePerson(responsiblePerson);
            task.setDueDate(LocalDate.parse(request.dueDate()));
            task.setTaskType(TaskType.valueOf(request.taskType().toUpperCase()));
            task.setDaysOverdue(daysOverdue);
        } else {
            throw new AccessDeniedException("You are not part of this task");
        }
        return taskMapper.toResponse(taskRepository.save(task));
    }

    // only for admins
    @Transactional
    public void delete (Long taskId){
        taskRepository.deleteById(taskId);
    }


}
