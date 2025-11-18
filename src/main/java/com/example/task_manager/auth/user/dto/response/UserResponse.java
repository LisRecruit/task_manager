package com.example.task_manager.auth.user.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record UserResponse(Long id,
                           String userName,
                           Boolean enabled,
                           List<String> roleNames) {
}
