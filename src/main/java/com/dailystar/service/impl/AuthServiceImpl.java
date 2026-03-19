package com.dailystar.service.impl;

import com.dailystar.dao.AccountProfileDao;
import com.dailystar.dao.AccountFollowDao;
import com.dailystar.dao.AuthAccountDao;
import com.dailystar.dao.CommunityPostDao;
import com.dailystar.dao.CommunityPostLikeDao;
import com.dailystar.dto.AuthCurrentUserResponse;
import com.dailystar.dto.AuthLoginRequest;
import com.dailystar.dto.AuthLoginResponse;
import com.dailystar.dto.AuthProfileUpdateRequest;
import com.dailystar.dto.AuthRegisterRequest;
import com.dailystar.entity.AccountProfileEntity;
import com.dailystar.entity.AuthAccountEntity;
import com.dailystar.enums.AuthAccountStatusEnum;
import com.dailystar.enums.GenderEnum;
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
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AccountProfileDao accountProfileDao;
    private final AccountFollowDao accountFollowDao;
    private final AuthAccountDao authAccountDao;
    private final CommunityPostDao communityPostDao;
    private final CommunityPostLikeDao communityPostLikeDao;
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
        accountProfileDao.insertSelective(
            AccountProfileEntity.builder()
                .accountId(entity.getId())
                .createdAt(now)
                .updatedAt(now)
                .build()
        );
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
        return buildCurrentUserResponse(entity, ensureProfile(entity.getId()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AuthCurrentUserResponse updateProfile(Long accountId, AuthProfileUpdateRequest request) {
        if (accountId == null) {
            throw new BusinessException(MessageCodeEnum.UNAUTHORIZED);
        }
        AuthAccountEntity entity = authAccountDao.selectById(accountId)
            .orElseThrow(() -> new BusinessException(MessageCodeEnum.ACCOUNT_NOT_FOUND));
        AccountProfileEntity profile = ensureProfile(accountId);
        GenderEnum gender = resolveGender(request.getGender());
        profile.setNickname(request.getNickname().trim());
        profile.setAvatar(request.getAvatar().trim());
        profile.setGender(gender);
        profile.setUpdatedAt(LocalDateTime.now());
        accountProfileDao.updateById(profile);
        return buildCurrentUserResponse(entity, profile);
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

    private AccountProfileEntity ensureProfile(Long accountId) {
        return accountProfileDao.selectByAccountId(accountId).orElseGet(() -> {
            LocalDateTime now = LocalDateTime.now();
            AccountProfileEntity profile = AccountProfileEntity.builder()
                .accountId(accountId)
                .createdAt(now)
                .updatedAt(now)
                .build();
            accountProfileDao.insertSelective(profile);
            return profile;
        });
    }

    private AuthCurrentUserResponse buildCurrentUserResponse(AuthAccountEntity account, AccountProfileEntity profile) {
        String nickname = profile == null ? null : profile.getNickname();
        String avatar = profile == null ? null : profile.getAvatar();
        String gender = profile == null || profile.getGender() == null ? null : profile.getGender().name();
        return AuthCurrentUserResponse.builder()
            .accountId(account.getId())
            .mobile(account.getMobile())
            .status(account.getStatus().name())
            .nickname(nickname)
            .avatar(avatar)
            .gender(gender)
            .followerCount(accountFollowDao.countFollowers(account.getId()))
            .followingCount(accountFollowDao.countFollowing(account.getId()))
            .shareCount(communityPostDao.countByAccountId(account.getId()))
            .likeCount(communityPostLikeDao.countReceivedLikesByAccountId(account.getId()))
            .profileCompleted(StringUtils.hasText(nickname) && StringUtils.hasText(avatar))
            .build();
    }

    private GenderEnum resolveGender(String value) {
        GenderEnum gender = GenderEnum.fromCode(value);
        if (value != null && !value.trim().isEmpty() && gender == null) {
            throw new BusinessException(MessageCodeEnum.PARAM_ERROR, "性别字段不合法");
        }
        return gender;
    }
}
