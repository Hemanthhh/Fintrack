package com.fintrack.users.service;

import com.fintrack.users.config.ApplicationConfig;
import com.fintrack.users.config.JwtTokenService;
import com.fintrack.users.dto.UserLoginRequest;
import com.fintrack.users.dto.UserLoginResponse;
import com.fintrack.users.dto.UserRegistrationRequest;
import com.fintrack.users.dto.UserResponse;
import com.fintrack.users.model.User;
import com.fintrack.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import static org.springframework.security.core.userdetails.User.withUsername;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ApplicationConfig applicationConfig;
    private final JwtTokenService jwtTokenService;

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
                .password(applicationConfig.passwordEncoder().encode(request.password()))
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
        if (!applicationConfig.passwordEncoder().matches(request.password(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        // Create UserDetails (using Spring Security's User)
        UserDetails userDetails = withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities("USER") // or fetch roles if you have them
                .build();

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
        return new UserResponse(
                "currentUserId",
                "currentUsername",
                "currentEmail");

    }
}
