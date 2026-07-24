package com.example.springbootdemo.service;

import com.example.springbootdemo.entity.User;
import com.example.springbootdemo.mapper.UserMapper;
import org.springframework.stereotype.Service;
import java.util.List;

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
    //添加一个List相关的方法
    public List<User> getAllUsers(){
        return userMapper.selectAll();
    }
}
