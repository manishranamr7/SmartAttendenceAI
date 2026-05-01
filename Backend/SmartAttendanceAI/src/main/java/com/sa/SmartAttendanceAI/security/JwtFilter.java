package com.sa.SmartAttendanceAI.security;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import jakarta.servlet.FilterChain;
import jakarta.servlet.GenericFilter;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtFilter extends GenericFilter {
    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                String email = jwtUtil.extractEmail(token);
                String role = jwtUtil.extractRole(token);

                if (email != null && role != null) {
                    String normalizedRole = role.trim().toUpperCase(Locale.ROOT);
                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(email, null,
                                    List.of(() -> "ROLE_" + normalizedRole));

                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (Exception ex) {
                SecurityContextHolder.clearContext();
            }
        }

        chain.doFilter(req, res);
    }
}
