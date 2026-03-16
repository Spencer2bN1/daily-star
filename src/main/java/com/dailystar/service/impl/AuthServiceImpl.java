package com.dailystar.service.impl;

import com.dailystar.dao.AuthAccountDao;
import com.dailystar.dto.AuthCurrentUserResponse;
import com.dailystar.dto.AuthLoginRequest;
import com.dailystar.dto.AuthLoginResponse;
import com.dailystar.dto.AuthRegisterRequest;
import com.dailystar.entity.AuthAccountEntity;
import com.dailystar.enums.AuthAccountStatusEnum;
import com.dailystar.enums.LoginTypeEnum;
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
            throw new BusinessException("MOBILE_EXISTS", "手机号已注册");
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
            .orElseThrow(() -> new BusinessException("ACCOUNT_NOT_FOUND", "手机号未注册"));
        if (entity.getStatus() != AuthAccountStatusEnum.ACTIVE) {
            throw new BusinessException("ACCOUNT_DISABLED", "账号不可用");
        }
        if (!passwordEncoder.matches(request.getPassword(), entity.getPasswordHash())) {
            throw new BusinessException("PASSWORD_ERROR", "手机号或密码错误");
        }
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setLastLoginAt(LocalDateTime.now());
        authAccountDao.updateById(entity);
        return buildLoginResponse(entity);
    }

    @Override
    public AuthCurrentUserResponse currentUser(Long accountId) {
        if (accountId == null) {
            throw new BusinessException("UNAUTHORIZED", "请先登录");
        }
        AuthAccountEntity entity = authAccountDao.selectById(accountId)
            .orElseThrow(() -> new BusinessException("ACCOUNT_NOT_FOUND", "账号不存在"));
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
