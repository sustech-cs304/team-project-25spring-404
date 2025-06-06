package com.training.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.training.mapper.UserMapper;
import com.training.model.User;
import com.training.service.UserService;
import com.training.util.MD5Utils;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User getUserById(Integer id) {
        return userMapper.selectUserById(id);
    }

    @Override
    public User getUserByUsername(String username) {
        return userMapper.selectUserByUsername(username);
    }

    @Override
    public List<User> getAllUsers() {
        return userMapper.selectAll();
    }

    @Override
    public User login(String username, String password) {
        User user = getUserByUsername(username);
        if (user != null && user.getPassword().equals(MD5Utils.encode(password))) {
            // 返回用户信息，但去除密码
            user.setPassword(null);
            return user;
        }
        return null;
    }

    @Override
    public int register(User user) {
        // 检查用户名是否已存在
        if (getUserByUsername(user.getUsername()) != null) {
            return 0;
        }
        
        // 密码加密
        user.setPassword(MD5Utils.encode(user.getPassword()));
        
        // 默认头像
        if (user.getAvatar() == null) {
            user.setAvatar("default.jpg");
        }
        
        // 如果昵称为空，则使用用户名
        if (user.getNickname() == null) {
            user.setNickname(user.getUsername());
        }
        
        return userMapper.insert(user);
    }

    @Override
    public int updateUser(User user) {
        // 如果需要修改密码
        if (user.getPassword() != null) {
            user.setPassword(MD5Utils.encode(user.getPassword()));
        }
        return userMapper.update(user);
    }

    @Override
    public int deleteUser(Integer id) {
        return userMapper.deleteById(id);
    }
} 