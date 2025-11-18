package com.example.task_manager.auth.security;




import com.example.task_manager.auth.user.CustomUserDetails;
import com.example.task_manager.auth.user.User;
import com.example.task_manager.auth.user.UserRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class);

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    public JwtRequestFilter(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            chain.doFilter(request, response);
            return;
        }
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            logger.info("CORS preflight request detected from Origin: {}", request.getHeader("Origin"));
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }
        String origin = request.getHeader("Origin");
        String path = request.getRequestURI();
        logger.info("CORS check — Origin: {}",origin);
        logger.info("CORS check — Path: {}",path);

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                String username = jwtUtil.extractUsername(token);
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    User user = userRepository.findByUserName(username)
                            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
                    CustomUserDetails userDetails = new CustomUserDetails(user);
                    if (Boolean.TRUE.equals(jwtUtil.validateToken(token, userDetails))) {
                        UsernamePasswordAuthenticationToken authenticationToken =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                        logger.info("Authenticated user: {}", username);
                        logger.info("token: {}", token);
                    }
                }
            } catch (JwtException | UsernameNotFoundException e) {
                logger.warn("JWT error: {}", e.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
                return;
            }
        }
        chain.doFilter(request, response);
    }

}
