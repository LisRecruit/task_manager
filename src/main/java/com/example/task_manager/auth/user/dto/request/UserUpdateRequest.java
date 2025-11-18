package com.example.task_manager.auth.user.dto.request;

import java.util.List;

public record UserUpdateRequest(String username,
                                Boolean enabled,
                                List<Long> roleIds) {
}
