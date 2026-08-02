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
    //添加一个插入用户的方法
    public User createUser(User user){
        int affectedRows= userMapper.insert(user);

        if(affectedRows!=1){
            throw new RuntimeException("插入用户失败");
        }
        return user;
    }
    //添加一个更新用户的方法
    public User updateUser(User user){
        int affectedRows= userMapper.update(user);
        if(affectedRows!=1){
            throw new IllegalStateException("修改用户失败");
        }
        return userMapper.selectById(user.getId());
    }
    //添加一个删除用户的方法
    public void deleteUser(Long id){
        int affectedRows= userMapper.deleteById(id);
        if(affectedRows!=1){
            throw new IllegalStateException("删除用户失败");
        }
    }
}
