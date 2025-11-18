package com.example.task_manager.auth;


import com.example.task_manager.auth.role.Role;
import com.example.task_manager.auth.role.RoleService;
import com.example.task_manager.auth.security.JwtUtil;
import com.example.task_manager.auth.user.User;
import com.example.task_manager.auth.user.UserService;
import com.example.task_manager.auth.user.dto.request.UserCreateRequest;
import com.example.task_manager.auth.user.dto.request.UserLoginRequest;
import com.example.task_manager.auth.user.dto.response.LoginResponse;
import com.example.task_manager.auth.user.dto.response.RegistrationResponse;
import com.example.task_manager.auth.user.dto.response.UserResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/auth")

public class AuthController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final RoleService roleService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login (@RequestBody UserLoginRequest request){
        try {
            User user = userService.getUserByUsername(request.username());
            System.out.printf("Login request: %s", request);
            System.out.println("User: " + user);
            if (user != null && !user.getEnabled()) {
                throw new RuntimeException("User is not verified");
            }

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password())
            );
            System.out.printf("Trying to get user from db");


            UserDetails userDetails = userDetailsService.loadUserByUsername(request.username());

            Long userId = user.getId();

            String token = jwtUtil.generateToken(userDetails, userId);
            LoginResponse loginResponse = LoginResponse.builder()
                    .message("Login successful")
                    .token(token)
                    .build();

            return ResponseEntity.ok(loginResponse);
        } catch (BadCredentialsException | UsernameNotFoundException | EntityNotFoundException e) {
            throw new RuntimeException("Invalid userName or password");
        } catch (RuntimeException e) {
            throw e;
        }

    }
    @PostMapping("/registration")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<RegistrationResponse> registartion (@RequestBody UserCreateRequest request){
        System.out.println("Registration request: " + request);

        if (!ValidatorAuth.isValidPassword(request.password())) {
            throw new RuntimeException("Password must contain at least 8 characters, including digits, " +
                    "uppercase and lowercase letters.");
        }
        System.out.println("Date is valid");

        User createdUser = userService.createUser(request);
        if (createdUser == null) {
            throw new RuntimeException("User already exists");
        }
        System.out.println("Created user: " + createdUser);

        UserResponse userResponse = UserResponse.builder()
                .id(createdUser.getId())
                .userName(request.username())
                .enabled(createdUser.getEnabled())
                .roleNames( createdUser.getRoles().stream()
                        .map(Role::getName)
                        .toList()
                )
                .build();
        RegistrationResponse response = RegistrationResponse.builder()
//                .token(token)
                .userResponse(userResponse)
                .message("User with userName " + createdUser.getUserName() + " created with ID " + createdUser.getId())
                .build();
        return ResponseEntity.ok(response);

    }
}
