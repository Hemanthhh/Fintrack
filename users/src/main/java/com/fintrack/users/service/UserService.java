package com.fintrack.users.service;

import com.fintrack.users.dto.UserLoginRequest;
import com.fintrack.users.dto.UserLoginResponse;
import com.fintrack.users.dto.UserRegistrationRequest;
import com.fintrack.users.dto.UserResponse;

public interface UserService {

    UserResponse register(UserRegistrationRequest request);

    UserLoginResponse login(UserLoginRequest request);

    UserResponse getCurrentUser();
}
