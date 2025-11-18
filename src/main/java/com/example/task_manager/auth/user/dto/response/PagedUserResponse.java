package com.example.task_manager.auth.user.dto.response;

import lombok.Builder;

import java.util.List;
@Builder
public record PagedUserResponse(List<UserResponse> users,
                                int page,
                                int pageSize,
                                long total  ) {
}
