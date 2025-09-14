package com.anubhav.airbnb.services;

import com.anubhav.airbnb.config.JwtConfig;
import com.anubhav.airbnb.enums.Role;
import com.anubhav.airbnb.models.User;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService
{
    private final JwtConfig jwtConfig;

    @Override
    public String generateAccessToken(User user)
    {
        return generateToken(user, jwtConfig.getAccessTokenExpiration());
    }

    @Override
    public String generateRefreshToken(User user)
    {
        return generateToken(user, jwtConfig.getRefreshTokenExpiration());
    }

    @Override
    public boolean validateToken(String token)
    {
        try
        {
            Claims claims = getClaims(token);
            return claims.getExpiration().after(new Date());
        }
        catch (JwtException ex)
        {
            return false;
        }
    }

    @Override
    public String getEmailFromToken(String token)
    {
        Claims claims = getClaims(token);
        return claims.get("email", String.class);
    }

    @Override
    public Set<Role> getRolesFromToken(String token)
    {
        Claims claims = getClaims(token);

        // roles claim as a String like "[ADMIN, USER]"
        String rolesString = claims.get("roles", String.class);

        // remove brackets and split by comma -> "ADMIN, USER"
        // trim -> "ADMIN", "USER"
        return Arrays.stream(
                        rolesString.replace("[", "")
                                .replace("]", "")
                                .split(","))
                .map(String::trim)       // remove extra spaces
                .map(Role::valueOf)      // convert String -> Enum
                .collect(Collectors.toSet());
    }

    private Claims getClaims(String token)
    {
        return Jwts.parser()
                .verifyWith(jwtConfig.getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private String generateToken(User user, long tokenExpiration)
    {
        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("name", user.getName())
                .claim("roles", user.getRoles().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + (1000 * tokenExpiration)))
                .signWith(jwtConfig.getSecretKey())
                .compact();
    }
}
