package com.training.service;

import java.util.List;

import com.training.model.User;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 根据用户ID查询用户
     * 
     * @param id 用户ID
     * @return 用户信息
     */
    User getUserById(Integer id);
    
    /**
     * 根据用户名查询用户
     * 
     * @param username 用户名
     * @return 用户信息
     */
    User getUserByUsername(String username);
    
    /**
     * 查询所有用户
     * 
     * @return 用户列表
     */
    List<User> getAllUsers();
    
    /**
     * 用户登录
     * 
     * @param username 用户名
     * @param password 密码
     * @return 用户信息
     */
    User login(String username, String password);
    
    /**
     * 用户注册
     * 
     * @param user 用户信息
     * @return 结果
     */
    int register(User user);
    
    /**
     * 修改用户
     * 
     * @param user 用户信息
     * @return 结果
     */
    int updateUser(User user);
    
    /**
     * 删除用户
     * 
     * @param id 用户ID
     * @return 结果
     */
    int deleteUser(Integer id);
} 