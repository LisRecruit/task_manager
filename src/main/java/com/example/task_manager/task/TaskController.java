package com.example.task_manager.task;

import com.example.task_manager.auth.security.JwtUtil;
import com.example.task_manager.auth.user.UserService;
import com.example.task_manager.task.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/task")
public class TaskController {
    private final TaskService taskService;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    @PostMapping("/create")
    public ResponseEntity<TaskResponse> createTask (@RequestBody CreateTaskRequest request,
                                                    @RequestHeader("Authorization") String token) {
        System.out.println("TOKEN: "+token);
        System.out.println("Token bytes: " + Arrays.toString(token.getBytes(StandardCharsets.UTF_8)));
        Long userId = jwtUtil.extractUserId(token);
        TaskResponse response = taskService.createTask(request, userId);
        return ResponseEntity.ok(response);
    }
    @PatchMapping("/edit")
    public ResponseEntity<TaskResponse> editTask (@RequestBody UpdateTaskRequest request,
                                                  @RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.extractUserId(token);
        TaskResponse response = taskService.updateTask(request, userId);
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteTask (@PathVariable long id){
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/complete/{taskId}")
    public ResponseEntity<CompleteTaskResponse> completeTask (@PathVariable long taskId,
                                                              @RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.extractUserId(token);
        CompleteTaskResponse response = taskService.completeTask(taskId, userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/create/{taskId}")
    public ResponseEntity<TaskResponse> createSubTask(@PathVariable long taskId, @RequestBody CreateTaskRequest request,
                                                      @RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.extractUserId(token);
        TaskResponse response = taskService.createSubTask(request, userId, taskId);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById (@PathVariable long taskId){
        TaskResponse response = taskService.getTaskById(taskId);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/all")
    public ResponseEntity<List<TaskResponse>> getMyTasks (
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) Boolean taskComplete,
            @RequestParam(required = false) String taskType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDateTo
    ) {
        String jwt = token.startsWith("Bearer ") ? token.substring(7) : token;
        Long userId = jwtUtil.extractClaim(jwt, claims -> claims.get("user_id", Long.class));
//        if (taskComplete == null) {
//            taskComplete = false;
//        }
        TaskFilter filter = new TaskFilter(taskComplete, dueDateFrom, dueDateTo, taskType);
        List<TaskResponse> response = taskService.getMyTasks(userId, filter);
        System.out.println(response);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/all-subs")
    public ResponseEntity<List<TaskResponse>> getSubordinatesTasks(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) Boolean taskComplete,
            @RequestParam(required = false) String taskType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDateTo) {
        Long userId = jwtUtil.extractUserId(token);
//        if (taskComplete == null) {
//            taskComplete = false;
//        }
        TaskFilter filter = new TaskFilter(taskComplete, dueDateFrom, dueDateTo, taskType);
        List<TaskResponse> response = taskService.getTasksForUserAndSubordinates(userId, filter);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/dependencies/{id}")
    public ResponseEntity<List<DependencyStatusResponse>> getDependencyStatus(@PathVariable long id){
        List<DependencyStatusResponse> responses = taskService.getDependencyStatus(id);
        return ResponseEntity.ok(responses);
    }

}
