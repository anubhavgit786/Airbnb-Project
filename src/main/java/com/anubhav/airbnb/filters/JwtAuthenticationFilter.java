package com.anubhav.airbnb.filters;

import com.anubhav.airbnb.enums.Role;
import com.anubhav.airbnb.models.User;
import com.anubhav.airbnb.services.JwtService;
import com.anubhav.airbnb.services.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter
{
    private final JwtService jwtService;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException
    {
        String authHeader = request.getHeader("Authorization");
        if(authHeader == null || !authHeader.startsWith("Bearer "))
        {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        if(!jwtService.validateToken(token))
        {
            filterChain.doFilter(request, response);
            return;
        }

        String email = jwtService.getEmailFromToken(token);
        User user = userService.getUserByEmail(email);

        Set<Role> roles = jwtService.getRolesFromToken(token);

        // Convert each Role -> SimpleGrantedAuthority
        Set<SimpleGrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toSet());

        var authentication = new UsernamePasswordAuthenticationToken(
                user,
                null,
                authorities // 👈 multiple authorities
        );



        authentication.setDetails( new WebAuthenticationDetailsSource().buildDetails(request));

        if(SecurityContextHolder.getContext().getAuthentication() == null)
        {
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);

    }
}
