package com.example.springbootdemo.service;

import com.example.springbootdemo.entity.User;
import com.example.springbootdemo.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

//注解表示这是一个业务类
@Service
public class UserService {
    //定义mapper
    private final UserMapper userMapper;

    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public User getUserById( Long id){
        return userMapper.selectById(id);
    }
}
