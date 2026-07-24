package com.example.springbootdemo.controller;

import com.example.springbootdemo.entity.User;
import com.example.springbootdemo.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    //需要一个私有的userservice变量
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    //get方法，获取动态的id,用{}包起来的是动态路径
    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id){
        return userService.getUserById(id);
    }
    //接收所有用户的方法
    @GetMapping
    public List<User> getAllUsers(){
        return userService.getAllUsers();
    }
}
