package com.dailystar.service.impl;

import com.dailystar.dao.AuthAccountDao;
import com.dailystar.dto.AuthCurrentUserResponse;
import com.dailystar.dto.AuthLoginRequest;
import com.dailystar.dto.AuthLoginResponse;
import com.dailystar.dto.AuthRegisterRequest;
import com.dailystar.entity.AuthAccountEntity;
import com.dailystar.enums.AuthAccountStatusEnum;
import com.dailystar.enums.LoginTypeEnum;
import com.dailystar.enums.MessageCodeEnum;
import com.dailystar.exception.BusinessException;
import com.dailystar.model.LoginUser;
import com.dailystar.service.AuthService;
import com.dailystar.util.JwtTokenUtil;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthAccountDao authAccountDao;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AuthLoginResponse register(AuthRegisterRequest request) {
        authAccountDao.selectByMobile(request.getMobile()).ifPresent(account -> {
            throw new BusinessException(MessageCodeEnum.MOBILE_EXISTS);
        });

        LocalDateTime now = LocalDateTime.now();
        AuthAccountEntity entity = AuthAccountEntity.builder()
            .mobile(request.getMobile())
            .passwordHash(passwordEncoder.encode(request.getPassword()))
            .status(AuthAccountStatusEnum.ACTIVE)
            .createdAt(now)
            .updatedAt(now)
            .lastLoginAt(now)
            .build();
        authAccountDao.insertSelective(entity);
        return buildLoginResponse(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AuthLoginResponse login(AuthLoginRequest request) {
        AuthAccountEntity entity = authAccountDao.selectByMobile(request.getMobile())
            .orElseThrow(() -> new BusinessException(MessageCodeEnum.ACCOUNT_NOT_FOUND, "手机号未注册"));
        if (entity.getStatus() != AuthAccountStatusEnum.ACTIVE) {
            throw new BusinessException(MessageCodeEnum.ACCOUNT_DISABLED);
        }
        if (!passwordEncoder.matches(request.getPassword(), entity.getPasswordHash())) {
            throw new BusinessException(MessageCodeEnum.PASSWORD_ERROR);
        }
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setLastLoginAt(LocalDateTime.now());
        authAccountDao.updateById(entity);
        return buildLoginResponse(entity);
    }

    @Override
    public AuthCurrentUserResponse currentUser(Long accountId) {
        if (accountId == null) {
            throw new BusinessException(MessageCodeEnum.UNAUTHORIZED);
        }
        AuthAccountEntity entity = authAccountDao.selectById(accountId)
            .orElseThrow(() -> new BusinessException(MessageCodeEnum.ACCOUNT_NOT_FOUND));
        return AuthCurrentUserResponse.builder()
            .accountId(entity.getId())
            .mobile(entity.getMobile())
            .status(entity.getStatus().name())
            .build();
    }

    private AuthLoginResponse buildLoginResponse(AuthAccountEntity entity) {
        String token = jwtTokenUtil.generateToken(LoginUser.builder()
            .accountId(entity.getId())
            .mobile(entity.getMobile())
            .loginType(LoginTypeEnum.MOBILE_PASSWORD)
            .build());
        return AuthLoginResponse.builder()
            .accountId(entity.getId())
            .mobile(entity.getMobile())
            .token(token)
            .tokenType("Bearer")
            .expiresIn(jwtTokenUtil.getExpireSeconds())
            .build();
    }
}
