package com.example.task_manager.auth.user;

import com.example.task_manager.auth.role.Role;
import com.example.task_manager.auth.role.RoleMapper;
import com.example.task_manager.auth.user.dto.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", uses = RoleMapper.class,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
    @Mapping(target = "roleNames", source = "roles")
    UserResponse userToUserResponse(User user);

    List<UserResponse> usersToUserResponses(List<User> users);

    default String mapRoleToName(Role role) {
        return role.getName();
    }

}
