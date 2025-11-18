package com.example.task_manager.auth;

import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class ValidatorAuth {

    public static boolean isValidPassword(String password) {
        final String PASSWORD_PATTERN =  "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$";
        final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        if (password == null) {
            return false;
        }
        return pattern.matcher(password).matches();
    }


}
