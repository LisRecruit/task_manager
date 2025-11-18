package com.example.task_manager.auth.role;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    default Long roleToRoleId(Role role) {
        return role != null ? role.getId() : null;
    }

    List<Long> rolesToRoleIds(List<Role> roles);
}
