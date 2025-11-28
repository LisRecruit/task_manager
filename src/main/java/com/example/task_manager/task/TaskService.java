package com.example.task_manager.task;

import com.example.task_manager.auth.user.User;
import com.example.task_manager.auth.user.UserService;
import com.example.task_manager.task.dto.CompleteTaskResponse;
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
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserService userService;
    private final TaskMapper taskMapper;

    //get userCreatorId from token
    @Transactional
    public TaskResponse createTask (CreateTaskRequest request, Long userCreatorId) {
        //validate request.dueDate
        //validate request.taskType
        Task task = createSingleTask(request, userCreatorId);
        return taskMapper.toResponse(taskRepository.save(task));
    }

    // get requesterId from token
    @Transactional
    public TaskResponse updateTask (UpdateTaskRequest request, Long requesterId) {
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
            throw new AccessDeniedException("You are not eligible to do this changes");
        }
        return taskMapper.toResponse(taskRepository.save(task));
    }


    // only for admins
    @Transactional
    public void delete (Long taskId){
        taskRepository.deleteById(taskId);
    }

    @Transactional
    public CompleteTaskResponse completeTask (Long taskId, Long responsibleUserId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));
        if (responsibleUserId.equals(task.getResponsiblePerson().getId())){
            task.setTaskComplete(true);
            task.setCompletionDate(LocalDate.now());
            task.setDaysOverdue((int) ChronoUnit.DAYS.between(task.getDueDate(), LocalDate.now()));
            Task newTask = null;
            if (Boolean.TRUE.equals(task.getRepeatable())){
                LocalDate nextDueDate = determineNextDueDate(task.getRepeatableType(), task.getDueDate());
                newTask = taskRepository.save(repeatTask(task, nextDueDate));
            }
            Task savedTask = taskRepository.save(task);
            return new CompleteTaskResponse(
                    taskMapper.toResponse(savedTask),
                    newTask != null ? taskMapper.toResponse(newTask) : null
            );
        } else {
            throw new AccessDeniedException("You are not eligible to complete this task");
        }
    }

    @Transactional
    public TaskResponse createSubTask (CreateTaskRequest request, Long userCreatorId, Long parentTaskId) {
        Task parentTask = taskRepository.findById(parentTaskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));
        Task subTask = createSingleTask(request, userCreatorId);
        parentTask.getSubTasks().add(subTask);
        taskRepository.save(parentTask);
        return taskMapper.toResponse(taskRepository.save(subTask));
    }

    //get id from token
    @Transactional(readOnly = true)
    public List<TaskResponse> getMyTasks (Long responsiblePersonId) {
        List<Task> tasks = taskRepository.findAllByResponsiblePersonId(responsiblePersonId);
        return tasks.stream()
                .map(taskMapper::toResponse)
                .toList();
    }
    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksForUserAndSubordinates(Long userId) {
        User user = userService.getUserById(userId);
        List<Long> allIds = getAllSubordinateIdsIncludingSelf(user);
        List<Task> tasks = taskRepository.findAllByResponsiblePersonIdIn(allIds);
        return tasks.stream()
                .map(taskMapper::toResponse)
                .toList();
    }

    //recursion to get all subordinates
    private List<Long> getAllSubordinateIdsIncludingSelf(User user) {
        List<Long> ids = new ArrayList<>();
        ids.add(user.getId());
        collectSubordinates(user, ids);
        return ids;
    }
    private void collectSubordinates(User user, List<Long> ids) {
        for (User sub : user.getSubordinates()) {
            ids.add(sub.getId());
            collectSubordinates(sub, ids); // recursion
        }
    }



    private Task createSingleTask (CreateTaskRequest request, Long userCreatorId){
        User taskSetBy = userService.getUserById(userCreatorId);
        User responsiblePerson = userService.getUserById(request.responsiblePersonId());
        LocalDate dueDate = LocalDate.parse(request.dueDate());
        Integer daysOverdue = (int) ChronoUnit.DAYS.between(dueDate, LocalDate.now());
        //validate request.dueDate
        //validate request.taskType
        return Task.builder()
                .taskDescription(request.taskDescription())
                .responsiblePerson(responsiblePerson)
                .taskSetBy(taskSetBy)
                .dueDate(dueDate)
                .daysOverdue(daysOverdue)
                .taskComplete(false)
                .taskType(TaskType.valueOf(request.taskType().toUpperCase()))
                .repeatable(request.repeatable())
                .repeatableType(RepeatableType.valueOf(request.repeatableType().toUpperCase()))
                .build();
    }

    private Task repeatTask(Task task, LocalDate dueDate) {
        return Task.builder()
                .taskDescription(task.getTaskDescription())
                .responsiblePerson(task.getResponsiblePerson())
                .taskSetBy(task.getTaskSetBy())
                .dueDate(dueDate)
                .taskComplete(false)
                .taskType(task.getTaskType())
                .repeatable(task.getRepeatable())
                .repeatableType(task.getRepeatableType())
                .build();
    }

    private LocalDate determineNextDueDate(RepeatableType type, LocalDate currentDueDate) {
        return switch (type) {
            case DAILY -> currentDueDate.plusDays(1);
            case WEEKLY -> currentDueDate.plusWeeks(1);
            case BI_WEEKLY -> currentDueDate.plusWeeks(2);
            case MONTHLY -> currentDueDate.plusMonths(1);
        };
    }
    private int determineRepeatCount(RepeatableType type) {
        return switch (type) {
            case DAILY -> 30;
            case WEEKLY -> 48;
            case BI_WEEKLY -> 24;
            case MONTHLY -> 12;

        };
    }



}
