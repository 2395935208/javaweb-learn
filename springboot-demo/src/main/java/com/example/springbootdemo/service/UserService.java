package com.example.springbootdemo.service;

import com.example.springbootdemo.entity.User;
import com.example.springbootdemo.mapper.UserMapper;
import org.springframework.stereotype.Service;
import java.util.List;
import com.example.springbootdemo.dto.UserPageResult;
import com.example.springbootdemo.dto.UserOrderDTO;

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
    //动态sql查询
    public List<User> searchUsers(String keyword,Integer minAge){
        return userMapper.search(keyword,minAge);
    }
    //分页查询
    public UserPageResult getUserPage(int page,int pageSize){
        if(page<1){
            throw new IllegalArgumentException("page必须大于等于1");
        }

        if (pageSize < 1 || pageSize > 100) {
            throw new IllegalArgumentException("pageSize必须在1到100之间");
        }

        //定义offset
        int offset = (page - 1) * pageSize;

        List<User> records = userMapper.selectPage(offset, pageSize);
        long total = userMapper.count();
        long totalPages = (total + pageSize - 1) / pageSize;
        return new UserPageResult(
                records,
                total,
                page,
                pageSize,
                totalPages);
    }

    //批量查询
    public List<User> getUsersByIds(List<Long> ids){
        if(ids==null||ids.isEmpty()){
            throw new IllegalArgumentException("用户列表不能为空");
        }
        return userMapper.selectByIds(ids);
    }
    //添加一个调用多个表的方法
    public List<UserOrderDTO> getUserOrders() {
        return userMapper.selectUserOrders();
    }
}
