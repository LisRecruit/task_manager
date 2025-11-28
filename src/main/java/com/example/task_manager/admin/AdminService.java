package com.example.task_manager.admin;

import com.example.task_manager.auth.role.Role;
import com.example.task_manager.auth.role.RoleService;
import com.example.task_manager.auth.user.User;
import com.example.task_manager.auth.user.UserMapper;
import com.example.task_manager.auth.user.UserRepository;
import com.example.task_manager.auth.user.UserService;
import com.example.task_manager.auth.user.dto.response.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Service
public class AdminService {
    private final UserService userService;
    private final RoleService roleService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    @Autowired
    public AdminService(UserService userService, RoleService roleService, UserRepository userRepository,
                        UserMapper userMapper) {
        this.userService = userService;
        this.roleService = roleService;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }
    public ResponseEntity<List<UserResponse>> getUnapprovedUsers(){
        Role unapproved = roleService.getRoleById(3L);
        List<UserResponse> users = userService.getUsersByRole(unapproved);
        return  ResponseEntity.ok(users);
    }
    @Transactional
    public ResponseEntity<User> addRoleToUser(Long userId, Role role){
        User user = userService.getUserById(userId);
        if(!user.getRoles().contains(role)) {
            user.addRole(role);
            role.getUsers().add(user);
            userRepository.save(user);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(user);
    }
    @Transactional
    public ResponseEntity<User> removeRoleFromUser(Long userId, Role role){
        User user = userService.getUserById(userId);
        if (user.getRoles().contains(role)) {
            user.getRoles().remove(role);
            role.getUsers().remove(user);
            userRepository.save(user);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(user);
    }
    @Transactional
    public ResponseEntity<UserResponse> enableUser(Long userId){

        User user = userService.getUserById(userId);
        Role userRole = roleService.getRoleById(2L);
        Role notApprovedRole = roleService.getRoleById(3L);
        if (!user.getRoles().contains(userRole)) {
            user.getRoles().add(userRole);
            userRole.getUsers().add(user);
        }
        if (user.getRoles().contains(notApprovedRole)) {
            user.getRoles().remove(notApprovedRole);

            notApprovedRole.getUsers().remove(user);
        }
        user.setEnabled(true);
        userRepository.save(user);
        return ResponseEntity.ok(userMapper.userToUserResponse(user));
    }
  @Transactional
    public ResponseEntity<UserResponse> disableUser(Long userId){
        User user = userService.getUserById(userId);
        Role notApprovedRole = roleService.getRoleById(3L);
      for (Role role : new HashSet<>(user.getRoles())) {
          role.getUsers().remove(user);
      }
      user.getRoles().clear();
      user.getRoles().add(notApprovedRole);
      notApprovedRole.getUsers().add(user);

      user.setEnabled(false);
        userRepository.save(user);
        return ResponseEntity.ok(userMapper.userToUserResponse(user));
  }
}
