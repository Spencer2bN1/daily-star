package com.dailystar.service.impl;

import com.dailystar.dao.UserDao;
import com.dailystar.dto.UserCreateRequest;
import com.dailystar.dto.UserResponse;
import com.dailystar.entity.UserEntity;
import com.dailystar.enums.MessageCodeEnum;
import com.dailystar.enums.UserStatusEnum;
import com.dailystar.exception.BusinessException;
import com.dailystar.mapper.UserDataMapper;
import com.dailystar.service.UserService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final UserDataMapper userDataMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserResponse createUser(UserCreateRequest request) {
        UserEntity entity = UserEntity.builder()
            .username(request.getUsername())
            .nickname(request.getNickname())
            .status(UserStatusEnum.ACTIVE)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
        userDao.insertSelective(entity);
        return userDataMapper.toResponse(entity);
    }

    @Override
    public UserResponse getUserById(Long id) {
        UserEntity entity = userDao.selectByPrimaryKey(id)
            .orElseThrow(() -> new BusinessException(MessageCodeEnum.USER_NOT_FOUND));
        return userDataMapper.toResponse(entity);
    }

    @Override
    public List<UserResponse> listUsers() {
        return userDataMapper.toResponseList(userDao.selectAll());
    }
}
