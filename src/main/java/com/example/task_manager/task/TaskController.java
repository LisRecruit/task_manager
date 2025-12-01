package com.example.task_manager.task;

import com.example.task_manager.auth.security.JwtUtil;
import com.example.task_manager.auth.user.User;
import com.example.task_manager.auth.user.UserService;
import com.example.task_manager.task.dto.CompleteTaskResponse;
import com.example.task_manager.task.dto.CreateTaskRequest;
import com.example.task_manager.task.dto.TaskResponse;
import com.example.task_manager.task.dto.UpdateTaskRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Role;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
        Long userId = getUserIdFromToken(token);
        TaskResponse response = taskService.createTask(request, userId);
        return ResponseEntity.ok(response);
    }
    @PatchMapping("/edit")
    public ResponseEntity<TaskResponse> editTask (@RequestBody UpdateTaskRequest request,
                                                  @RequestHeader("Authorization") String token) {
        Long userId = getUserIdFromToken(token);
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
        Long userId = getUserIdFromToken(token);
        CompleteTaskResponse response = taskService.completeTask(taskId, userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/create/{taskId}")
    public ResponseEntity<TaskResponse> createSubTask(@PathVariable long taskId, @RequestBody CreateTaskRequest request,
                                                      @RequestHeader("Authorization") String token) {
        Long userId = getUserIdFromToken(token);
        TaskResponse response = taskService.createSubTask(request, userId, taskId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<List<TaskResponse>> getMyTasks (@RequestHeader("Authorization") String token) {
        String jwt = token.startsWith("Bearer ") ? token.substring(7) : token;
        Long userId = jwtUtil.extractClaim(jwt, claims -> claims.get("user_id", Long.class));
        List<TaskResponse> response = taskService.getMyTasks(userId);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/all-subs")
    public ResponseEntity<List<TaskResponse>> getSubordinatesTasks(@RequestHeader("Authorization") String token) {
        Long userId = getUserIdFromToken(token);
        List<TaskResponse> response = taskService.getTasksForUserAndSubordinates(userId);
        return ResponseEntity.ok(response);
    }

    private Long getUserIdFromToken(String token){
        String jwt = token.startsWith("Bearer ") ? token.substring(7) : token;
        return jwtUtil.extractClaim(jwt, claims -> claims.get("userId", Long.class));
    }

}
