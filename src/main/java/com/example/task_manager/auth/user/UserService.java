package com.example.task_manager.auth.user;

import com.example.task_manager.auth.role.Role;
import com.example.task_manager.auth.role.RoleRepository;
import com.example.task_manager.auth.security.JwtUtil;
import com.example.task_manager.auth.user.dto.request.PasswordUpdateRequest;
import com.example.task_manager.auth.user.dto.request.UserCreateRequest;
import com.example.task_manager.auth.user.dto.request.UserUpdateRequest;
import com.example.task_manager.auth.user.dto.response.PagedUserResponse;
import com.example.task_manager.auth.user.dto.response.UserResponse;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final JwtUtil jwtUtil;

    @Transactional
    public User createUser (UserCreateRequest request) {
        logger.info("Creating new user");
        if (userRepository.existsByUserName(request.username())){
            throw new IllegalArgumentException("User with this userName already exists.");
        }
        if (!request.password().equals(request.repeatPassword())){
            throw new IllegalArgumentException("Passwords do not match");
        }
        User user = User.builder()
                .userName(request.username())
                .password(passwordEncoder.encode(request.password()))
                .enabled(false)
                .build();

        Role notApprovedRole = roleRepository.findByName("NOTAPPROVED")
                .orElseThrow(() -> new EntityNotFoundException("Role NOTAPPROVED not found"));
        user.addRole(notApprovedRole);

        return userRepository.save(user);

    }

    @Transactional(readOnly = true)
    public User getUserByUsername (String username) {
        return userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException ("User with "+username+" notfound"));
    }

    //only for admins
    @Transactional
    public User updateUser (UserUpdateRequest request) {
        User user = userRepository.findByUserName(request.username())
                .orElseThrow(RuntimeException::new);

        user.setEnabled(request.enabled());
        List<Role> roles = roleRepository.findAllById(request.roleIds());
        user.getRoles().clear();
        user.getRoles().addAll(roles);

        return userRepository.save(user);
    }
    @Transactional
    public String updatePassword (PasswordUpdateRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(RuntimeException::new);
        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }
        if (!request.newPassword().equals(request.repeatNewPassword())){
            throw new RuntimeException("The new password and its confirmation do not match.");
        }
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
        return "Password updated successfully";

    }
    public Page<UserResponse> listAll (PageRequest pageRequest){
        return userRepository.findAll(pageRequest)
                .map(userMapper::userToUserResponse);

    }
    @Transactional(readOnly = true)
    public List<UserResponse> getUsersByRole (Role role){
        List<User> users = userRepository.findByRoles(role);
        return userMapper.usersToUserResponses(users);

    }
    @Transactional(readOnly = true)
    public User getUserById(Long userId) {
       return userRepository.findById(userId)  .orElseThrow(RuntimeException::new);
    }
    @Transactional
    public void deleteUserById(Long userId) {
        try {
            userRepository.deleteById(userId);
        } catch (EntityNotFoundException e) {
            throw new RuntimeException();
        }

    }

    @Transactional(readOnly = true)
    public PagedUserResponse getAllUsers(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<User> userPage = userRepository.findAll(pageable);

        List<UserResponse> userResponses = userMapper.usersToUserResponses(userPage.getContent());

        return PagedUserResponse.builder()
                .users(userResponses)
                .page(page)
                .pageSize(pageSize)
                .total(userPage.getTotalElements())
                .build();
    }
}
