package com.organization.taskManagement.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {


    private final JwtService jwtService;

    private final UserInfoService userInfoService;

    public JwtAuthFilter(JwtService jwtService, UserInfoService userInfoService) {
        this.jwtService = jwtService;
        this.userInfoService = userInfoService;
    }

    // Public APIs (No JWT needed)
    public static final String[] PUBLIC_URLS  = {
            "/api/auth/register",
            "/api/auth/register/send-otp",
            "/api/auth/register/verify-otp",
            "/generateToken",
            "/api/auth/forget-password",
            "/api/auth/reset-password",
            "/api/auth/login",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/error/**"
    };
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        for (String url : PUBLIC_URLS) {
            if (url.endsWith("/**") && requestURI.startsWith(url.replace("/**", ""))) {
                return true;
            } else if (url.endsWith("/") && requestURI.startsWith(url)) {
                return true;
            } else if (requestURI.equals(url)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, java.io.IOException {

        // Allow preflight requests
        if (request.getMethod().equalsIgnoreCase("OPTIONS")) {
            response.setStatus(HttpServletResponse.SC_OK);
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        String token = null;
        String employeeId = null;

        // Extract token
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        } else if (authHeader != null) {
            sendUnauthorizedError(response, "Invalid Authorization header format. Use: Bearer <token>");
            return;
        } else {
            sendUnauthorizedError(response, "Missing Authorization header");
            return;
        }

        // Extract email from token
        try {
            employeeId = jwtService.extractEmpId(token);
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            sendUnauthorizedError(response, "Token has expired");
            return;
        } catch (io.jsonwebtoken.SignatureException e) {
            sendUnauthorizedError(response, "Invalid token signature");
            return;
        } catch (Exception e) {
            // SEND 401 for any other JWT error
            sendUnauthorizedError(response, "Invalid token");
            return;
        }

        // Validate token
        if (employeeId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = userInfoService.loadUserByUsername(employeeId);
                if (jwtService.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities()
                            );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    sendUnauthorizedError(response, "Token validation failed");
                    return;
                }
            } catch (UsernameNotFoundException e) {
                sendUnauthorizedError(response, "User not found");
                return;
            } catch (Exception e) {
                sendUnauthorizedError(response, "Authentication failed");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private void sendUnauthorizedError(HttpServletResponse response, String message)
            throws java.io.IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        String jsonResponse = String.format(
                "{\"error\": \"Unauthorized\", \"message\": \"%s\", \"timestamp\": \"%s\"}",
                message,
                java.time.LocalDateTime.now()
        );

        response.getWriter().write(jsonResponse);
    }
}
