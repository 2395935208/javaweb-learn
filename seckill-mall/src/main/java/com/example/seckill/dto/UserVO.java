package com.example.seckill.dto;

import com.example.seckill.entity.User;
import java.time.LocalDateTime;

public record UserVO(Long id, String username, String phone, LocalDateTime createdAt) {
    public static UserVO from(User user) {
        return new UserVO(user.getId(), user.getUsername(), user.getPhone(), user.getCreatedAt());
    }
}
