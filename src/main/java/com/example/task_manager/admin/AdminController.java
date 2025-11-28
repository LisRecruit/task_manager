package com.example.task_manager.admin;

import com.example.task_manager.admin.dto.DeleteUserResponse;
import com.example.task_manager.admin.dto.EnableUserResponse;
import com.example.task_manager.auth.role.Role;
import com.example.task_manager.auth.role.RoleService;
import com.example.task_manager.auth.user.User;
import com.example.task_manager.auth.user.UserMapper;
import com.example.task_manager.auth.user.UserService;
import com.example.task_manager.auth.user.dto.response.PagedUserResponse;
import com.example.task_manager.auth.user.dto.response.UserResponse;
import com.example.task_manager.auth.user.dto.response.UsersResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/admin")
@RequiredArgsConstructor
@Tag(name = "Admin controller", description = "Admin control panel")
public class AdminController {
    private final AdminService adminService;
    private final RoleService roleService;
    private final UserService userService;
    private final UserMapper userMapper;

    @Operation(
            summary = "Get list of unapproved users",
            description = "Returns a list of users with 'enable' status set to false. Returns an empty list if no users are found",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of unapproved users (user enable status \"False\")",
                    content = @Content(schema = @Schema(implementation = UserResponse[].class))),
            }
    )
    @GetMapping("/users/getUnapproved")
    public ResponseEntity<UsersResponse> getUnapproved() {
        Role role = roleService.getRoleById(3L); //UNAPPROVED
        List<UserResponse> users = userService.getUsersByRole(role);
        return ResponseEntity.ok(new UsersResponse(users));
    }
    @Operation(
            summary = "Enable user for login",
            description = "Sets the user's 'enabled' status to true, allowing the user to log in",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User id and 'enabled' status set to true",
                            content = @Content(schema = @Schema(implementation = EnableUserResponse.class))),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    @PostMapping("/users/enable/{id}")
    public ResponseEntity<EnableUserResponse> enable(@PathVariable Long id) {
        try {
            adminService.enableUser(id);
            EnableUserResponse enableUserResponse = EnableUserResponse.builder()
                    .userId(id)
                    .userEnabled(true)
                    .build();
            return ResponseEntity.ok(enableUserResponse);
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("User not found");
        }
    }

    @Operation(
            summary = "Switch user access",
            description = "Enables or disables a user based on their current status. "
                    + "If the user is enabled, they will be disabled. "
                    + "If the user is disabled, they will be enabled.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User access switched successfully",
                            content = @Content(schema = @Schema(implementation = UserResponse.class))),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )

    @PatchMapping("/users/switchAccess/{id}")
    public ResponseEntity<UserResponse> switchAccess(@PathVariable Long id) {
        return Optional.ofNullable(userService.getUserById(id))
                .map(user -> user.getEnabled() != null && user.getEnabled()
                        ? adminService.disableUser(id).getBody()
                        : adminService.enableUser(id).getBody())
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }


    @Operation(
            summary = "Delete user",
            description = "Delete user by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User successfully deleted",
                            content = @Content(schema = @Schema(implementation = DeleteUserResponse.class))),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    @DeleteMapping("/users/{id}")
    public ResponseEntity<DeleteUserResponse> delete(@PathVariable Long id) {
        try {
            userService.deleteUserById(id);
            DeleteUserResponse deleteUserResponse = DeleteUserResponse.builder()
                    .message("User deleted")
                    .build();
            return ResponseEntity.ok(deleteUserResponse);
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("User not found");
        }
    }
    @Operation(
            summary = "Get user",
            description = "Get user by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = """
                                User JSON object.
                                Example:
                                {
                                    "id": 1,
                                    "username": "Homer Simpson",
                                    "enabled": true,
                                    "roleIds": [1, 2]
                                }
                            """,
                            content = @Content(schema = @Schema(implementation = UserResponse.class))),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    @GetMapping ("/users/{id}")
    public ResponseEntity<UserResponse> getUser (@PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            UserResponse response = userMapper.userToUserResponse(user);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("User not found");
        }
    }
    @Operation(
            summary = "Get paginated list of all users",
            description = "This endpoint retrieves a paginated list of users. You can specify the page and page size." +
                    " Optionally, you can filter users by their approval status (enabled). If no users are found," +
                    " a 404 error will be returned.",
            parameters = {
                    @Parameter(name = "page", description = "Page number", required = false, example = "1"),
                    @Parameter(name = "pageSize", description = "Number of users per page", required = false,
                            example = "30"),
                    @Parameter(name = "approved", description = "Filter users by their approval status (enabled)",
                            required = false, example = "true")
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "A paginated list of user objects.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PagedUserResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "No users found. The system currently has no users."
                    )
            }
    )

    @GetMapping("/users/listAll")
    public ResponseEntity<PagedUserResponse> getAllUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "30") int pageSize,
            @RequestParam(name = "approved", required = false) Boolean approved) {
        System.out.println("Received page: " + page + ", size: " + pageSize + ", approved: " + approved);
        PagedUserResponse pagedUserResponse;
        if (approved == null) {
            pagedUserResponse = userService.getAllUsers(page, pageSize);
        } else {
            if (pageSize == 0){
                pageSize = 30;
            }
            PagedUserResponse allUsersResponse = userService.getAllUsers(1, Integer.MAX_VALUE);
            List<UserResponse> allUsers = allUsersResponse.users();
            List<UserResponse> filteredUsers = allUsers.stream()
                    .filter(user -> user.enabled().equals(approved))
                    .toList();
            int start = Math.max((page - 1) * pageSize, 0);
            int end = Math.min(start + pageSize, filteredUsers.size());
            List<UserResponse> pagedList = (start >= filteredUsers.size())
                    ? List.of()
                    : filteredUsers.subList(start, end);
            int totalFiltered = filteredUsers.size();
            pagedUserResponse = new PagedUserResponse(pagedList, page, pageSize, totalFiltered);
        }
        return ResponseEntity.ok(pagedUserResponse);
    }

}
