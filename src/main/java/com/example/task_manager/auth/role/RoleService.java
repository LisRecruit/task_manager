package com.example.task_manager.auth.role;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    public Role getRoleById(Long id) {
        return roleRepository.findById(id).orElse(null);
    }
}
