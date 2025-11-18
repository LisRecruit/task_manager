package com.example.task_manager.auth.user.dto.request;

public record PasswordUpdateRequest(String oldPassword,
                                    String newPassword,
                                    String repeatNewPassword) {
}
