package com.dailystar.mapper;

import com.dailystar.dto.UserResponse;
import com.dailystar.entity.UserEntity;
import com.dailystar.util.LocalDateTimeUtils;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class UserDataMapper {

    public UserResponse toResponse(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        return UserResponse.builder()
            .id(entity.getId())
            .username(entity.getUsername())
            .nickname(entity.getNickname())
            .status(entity.getStatus() == null ? null : entity.getStatus().name())
            .createdAt(LocalDateTimeUtils.format(entity.getCreatedAt()))
            .updatedAt(LocalDateTimeUtils.format(entity.getUpdatedAt()))
            .build();
    }

    public List<UserResponse> toResponseList(List<UserEntity> entities) {
        if (entities == null) {
            return Collections.emptyList();
        }
        return entities.stream().map(this::toResponse).collect(Collectors.toList());
    }
}
