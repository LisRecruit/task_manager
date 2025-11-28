package com.example.task_manager.auth.user.dto.request;

public record UserCreateRequest(String username,
                                String password,
                                String repeatPassword) {
}
