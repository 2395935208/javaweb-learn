package com.example.springbootdemo.controller;

import com.example.springbootdemo.entity.User;
import com.example.springbootdemo.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.http.HttpStatus;

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
    //处理POST请求
    @PostMapping
    //成功时返回201 Created
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody User user){
        return userService.createUser(user);
    }
    //处理PUT请求
    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id,@RequestBody User user){
        user.setId(id);
        return userService.updateUser(user);
    }

    //处理DELETE请求
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id){
        userService.deleteUser(id);
    }
    //动态查询
    @GetMapping("/-search")
    public List<User> searchUsers(@RequestParam(required = false) String keyword,
                                  @RequestParam(required = false) Integer minAge
    ){
        return userService.searchUsers(keyword,minAge);

    }
}
