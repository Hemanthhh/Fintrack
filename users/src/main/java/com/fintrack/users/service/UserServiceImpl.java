package com.fintrack.users.service;

import com.fintrack.users.config.JwtTokenService;
import com.fintrack.users.dto.UserLoginRequest;
import com.fintrack.users.dto.UserLoginResponse;
import com.fintrack.users.dto.UserRegistrationRequest;
import com.fintrack.users.dto.UserResponse;
import com.fintrack.users.model.User;
import com.fintrack.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final UserDetailsService userDetailsService;

    @Override
    public UserResponse register(UserRegistrationRequest request) {
        // Check if user already exists
        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        var user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .createdAt(new Date())
                .build();

        userRepository.save(user);

        return new UserResponse(
                user.getId().toString(),
                user.getUsername(),
                user.getEmail());
    }

    @Override
    public UserLoginResponse login(UserLoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        // Validate password
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        // Use the existing UserDetailsService to get UserDetails
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());

        // Generate JWT token
        String token = jwtTokenService.generateToken(userDetails);

        // Build response
        return new UserLoginResponse(
                token,
                user.getId().toString(),
                user.getUsername(),
                user.getEmail());
    }

    @Override
    public UserResponse getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return new UserResponse(
                user.getId().toString(),
                user.getUsername(),
                user.getEmail());
    }
}
