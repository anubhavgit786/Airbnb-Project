package com.anubhav.airbnb.handlers;

import com.anubhav.airbnb.exceptions.ForbiddenException;
import com.anubhav.airbnb.exceptions.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler
{

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException
    {
        ForbiddenException ex = new ForbiddenException("You don’t have permission to access this resource");

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.getWriter().write("""
        {
            "status": "FORBIDDEN",
            "success": false,
            "message": "%s"
        }
        """.formatted(ex.getMessage()));
    }
}

