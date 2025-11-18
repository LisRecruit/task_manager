package com.example.task_manager.auth.user.dto.response;

import lombok.Builder;

@Builder
public record LoginResponse(String message, String token) {
}
