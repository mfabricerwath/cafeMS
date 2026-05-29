package com.mugisha.cafe.jwt;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService service;

    // ← Remplace les variables d'instance par ThreadLocal
    private static final ThreadLocal<Claims> threadLocalClaims = new ThreadLocal<>();
    private static final ThreadLocal<String> threadLocalUserName = new ThreadLocal<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {
            if(request.getServletPath().matches("/user/login|/user/forgotPassword|/user/signup")) {
                filterChain.doFilter(request, response);
            } else {
                String authorizationHeader = request.getHeader("Authorization");
                String token = null;

                if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                    token = authorizationHeader.substring(7);
                    threadLocalUserName.set(jwtUtil.extractUsername(token));
                    threadLocalClaims.set(jwtUtil.extractAllClaims(token));
                }

                String userName = threadLocalUserName.get();
                if(userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = service.loadUserByUsername(userName);
                    if(jwtUtil.validateToken(token, userDetails)) {
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
                        authToken.setDetails(
                                new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
                filterChain.doFilter(request, response);
            }
        } finally {
            // ← Nettoyage obligatoire après chaque requête
            threadLocalClaims.remove();
            threadLocalUserName.remove();
        }
    }
    public boolean isAdmin() {
        Claims claims = threadLocalClaims.get();
        return claims != null && 
               "admin".equalsIgnoreCase((String) claims.get("role"));
        
    }

    public boolean isUser() {
        Claims claims = threadLocalClaims.get();
        return claims != null && 
               "user".equalsIgnoreCase((String) claims.get("role"));
    }

    public String getCurrentUser() {
        return threadLocalUserName.get();
    }
}