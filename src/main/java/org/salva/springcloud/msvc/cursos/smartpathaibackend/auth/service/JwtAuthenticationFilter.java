package org.salva.springcloud.msvc.cursos.smartpathaibackend.auth.service;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        log.info("=== JWT Filter Debug ===");
        log.info("URI: {}", request.getRequestURI());
        log.info("Method: {}", request.getMethod());
        log.info("Auth Header: {}", authHeader != null ? authHeader.substring(0, Math.min(20, authHeader.length())) + "..." : "null");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.info("No Bearer token found, continuing filter chain");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String jwt = authHeader.substring(7);
            log.info("Token extracted (first 20 chars): {}...", jwt.substring(0, Math.min(20, jwt.length())));

            final String userEmail = jwtService.extractEmail(jwt);
            log.info("Email extracted from token: {}", userEmail);

            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                log.info("Loading user details for: {}", userEmail);

                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                log.info("User details loaded: {}", userDetails.getUsername());

                boolean isValid = jwtService.validateToken(jwt, userDetails.getUsername());
                log.info("Token validation result: {}", isValid);

                if (isValid) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.info("Authentication set successfully in SecurityContext");
                } else {
                    log.warn("Token validation failed");
                }
            } else if (userEmail == null) {
                log.warn("Email extracted from token is null");
            } else {
                log.info("User already authenticated");
            }
        } catch (Exception e) {
            log.error("Error processing JWT: {}", e.getMessage(), e);
        }

        log.info("=== End JWT Filter Debug ===\n");
        filterChain.doFilter(request, response);
    }
}
