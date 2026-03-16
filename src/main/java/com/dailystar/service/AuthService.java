package com.dailystar.service;

import com.dailystar.dto.AuthCurrentUserResponse;
import com.dailystar.dto.AuthLoginRequest;
import com.dailystar.dto.AuthLoginResponse;
import com.dailystar.dto.AuthRegisterRequest;

public interface AuthService {

    AuthLoginResponse register(AuthRegisterRequest request);

    AuthLoginResponse login(AuthLoginRequest request);

    AuthCurrentUserResponse currentUser(Long accountId);
}
