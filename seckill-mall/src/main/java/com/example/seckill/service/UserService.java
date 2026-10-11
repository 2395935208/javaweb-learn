package com.example.seckill.service;

import com.example.seckill.dto.LoginRequest;
import com.example.seckill.dto.RegisterRequest;
import com.example.seckill.dto.UserVO;

public interface UserService {
    UserVO register(RegisterRequest request);
    UserVO login(LoginRequest request);
}
