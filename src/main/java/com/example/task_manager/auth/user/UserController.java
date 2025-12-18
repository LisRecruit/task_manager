package com.example.task_manager.auth.user;

import com.example.task_manager.auth.user.dto.response.PagedUserResponse;
import com.example.task_manager.auth.user.dto.response.UserLookupResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/users")
public class UserController {
    private final UserService userService;

@GetMapping("/listAll")
public ResponseEntity<PagedUserResponse> getAllUsers(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "30") int pageSize) {

    PagedUserResponse pagedUserResponse = userService.getAllUsers(page, pageSize);
    return ResponseEntity.status(HttpStatus.OK).body(pagedUserResponse);
}

    @GetMapping("/search")
    public ResponseEntity<List<UserLookupResponse>> searchUsers(@RequestParam String query) {
        List<UserLookupResponse> users = userService.searchUsers(query);
        return ResponseEntity.ok(users);
    }


}
