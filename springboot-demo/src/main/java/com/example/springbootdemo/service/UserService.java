package com.example.springbootdemo.service;

import com.example.springbootdemo.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

//注解表示这是一个业务类
@Service
public class UserService {
    public User getuserById(@PathVariable Long id){
        return new User(id,"admin",123456);
    }
}
