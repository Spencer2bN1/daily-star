package com.dailystar.service;

import com.dailystar.dto.UserCreateRequest;
import com.dailystar.dto.UserResponse;
import java.util.List;

public interface UserService {

    UserResponse createUser(UserCreateRequest request);

    UserResponse getUserById(Long id);

    List<UserResponse> listUsers();
}
