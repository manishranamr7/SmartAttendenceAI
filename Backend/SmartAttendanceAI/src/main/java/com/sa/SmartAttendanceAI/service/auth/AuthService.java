package com.sa.SmartAttendanceAI.service.auth;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.sa.SmartAttendanceAI.dto.LoginRequest;
import com.sa.SmartAttendanceAI.dto.LoginResponse;
import com.sa.SmartAttendanceAI.entity.User;
import com.sa.SmartAttendanceAI.repository.UserRepository;
import com.sa.SmartAttendanceAI.security.JwtUtil;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        String normalizedRole = user.getRole() == null
                ? null
                : user.getRole().trim().toUpperCase(Locale.ROOT);

        if (normalizedRole == null || normalizedRole.isBlank()) {
            throw new RuntimeException("User role is not configured");
        }

        String token = jwtUtil.generateToken(user.getEmail(), normalizedRole);

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setRole(normalizedRole);

        return response;
    }
}
