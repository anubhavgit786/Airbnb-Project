package com.anubhav.airbnb.services;


import com.anubhav.airbnb.dtos.*;
import com.anubhav.airbnb.exceptions.ResourceConflictException;
import com.anubhav.airbnb.exceptions.ResourceNotFoundException;
import com.anubhav.airbnb.exceptions.UnauthorizedException;
import com.anubhav.airbnb.models.User;
import com.anubhav.airbnb.repositories.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import jakarta.servlet.http.Cookie;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService
{
    private final SessionService sessionService;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;


    @Override
    public User getUserByEmail(String email)
    {
        return userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    @Override
    public UserDto createUser(SignUpRequestDto request)
    {
        boolean emailExists = userRepository.existsByEmail(request.getEmail());

        if(emailExists)
        {
            throw new ResourceConflictException("User already exists with email: " + request.getEmail());
        }

        User user = modelMapper.map(request, User.class);
        String hash = passwordEncoder.encode(user.getPassword());
        user.setPassword(hash);
        User savedUser = userRepository.save(user);
        return this.modelMapper.map(savedUser, UserDto.class);
    }

    @Override
    public UserDto updateUser(UserUpdateDto request, HttpServletResponse response)
    {
        User user = getCurrentUser();

        boolean nameChanged  = !user.getName().equals(request.getName());

        user.setName(request.getName());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setGender(request.getGender());

        User updatedUser = userRepository.save(user);

        if(nameChanged)
        {
            sessionService.deleteAllSessions(user);
            deleteRefreshTokenFromCookie(response);
        }

        return modelMapper.map(updatedUser, UserDto.class);
    }

    @Override
    public void changePassword(ChangePasswordRequestDto request)
    {
        User user = getCurrentUser();

        if(!passwordEncoder.matches(request.getOldPassword(), user.getPassword()))
        {
            throw new UnauthorizedException("Invalid Credentials");
        }

        String hash = passwordEncoder.encode(request.getNewPassword());
        user.setPassword(hash);
        userRepository.save(user);
    }

    @Override
    public UserDto me()
    {
        var user = getCurrentUser();
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public JwtResponseDto refresh(String refreshToken)
    {
        if(!jwtService.validateToken(refreshToken))
        {
            throw new UnauthorizedException("Client must authenticate");
        }

        sessionService.validateSession(refreshToken);
        String email = jwtService.getEmailFromToken(refreshToken);
        User user = userRepository.findByEmail(email).orElseThrow(()-> new ResourceNotFoundException("User not found."));
        var accessToken = jwtService.generateAccessToken(user);
        return new JwtResponseDto(accessToken);
    }

    @Override
    public User getCurrentUser()
    {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return  (User) authentication.getPrincipal();
    }

    @Override
    public void logout(String refreshToken, HttpServletResponse response)
    {
        sessionService.validateSession(refreshToken);
        sessionService.deleteSession(refreshToken);
        deleteRefreshTokenFromCookie(response);
    }

    @Override
    public UserDto getMyProfile()
    {
        User user = getCurrentUser();
        return modelMapper.map(user, UserDto.class);
    }

    private void deleteRefreshTokenFromCookie(HttpServletResponse response)
    {
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        cookie.setPath("/auth/refresh");
        cookie.setSecure(true);
        response.addCookie(cookie);
    }
}
