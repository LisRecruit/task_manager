package com.example.task_manager.admin.dto;

import lombok.Builder;

@Builder
public record EnableUserResponse(Long userId,
                                 Boolean userEnabled) {
}
