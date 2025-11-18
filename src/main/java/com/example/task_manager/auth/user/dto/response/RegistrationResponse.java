package com.example.task_manager.auth.user.dto.response;

import lombok.Builder;

@Builder
public record RegistrationResponse(UserResponse userResponse, String message) {
}
