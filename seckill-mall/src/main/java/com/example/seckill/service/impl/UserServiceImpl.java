package com.example.seckill.service.impl;

import com.example.seckill.common.BusinessException;
import com.example.seckill.dto.LoginRequest;
import com.example.seckill.dto.RegisterRequest;
import com.example.seckill.dto.UserVO;
import com.example.seckill.entity.User;
import com.example.seckill.mapper.UserMapper;
import com.example.seckill.service.UserService;
import java.time.LocalDateTime;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserServiceImpl(UserMapper userMapper, BCryptPasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserVO register(RegisterRequest request) {
        if (userMapper.selectByUsername(request.username()) != null) {
            throw new BusinessException(409, "用户名已存在");
        }
        User user = new User();
        user.setUsername(request.username());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setPhone(request.phone());
        user.setCreatedAt(LocalDateTime.now().withNano(0));
        try {
            userMapper.insert(user);
        } catch (DuplicateKeyException exception) {
            // 两个请求同时通过查重时，数据库唯一约束仍保证只有一个注册成功。
            throw new BusinessException(409, "用户名已存在");
        }
        return UserVO.from(user);
    }

    @Override
    public UserVO login(LoginRequest request) {
        User user = userMapper.selectByUsername(request.username());
        if (user == null || !passwordMatches(request.password(), user.getPasswordHash())) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        return UserVO.from(user);
    }

    private boolean passwordMatches(String password, String passwordHash) {
        try {
            return passwordEncoder.matches(password, passwordHash);
        } catch (IllegalArgumentException exception) {
            // BCrypt 拒绝超出自身长度限制的输入时，也按登录失败返回。
            return false;
        }
    }
}
