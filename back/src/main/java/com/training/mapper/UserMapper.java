package com.training.mapper;

import java.util.List;

import com.training.model.User;

/**
 * 用户Mapper接口
 */
public interface UserMapper {
    
    /**
     * 根据用户ID查询用户
     * 
     * @param id 用户ID
     * @return 用户信息
     */
    User selectUserById(Integer id);
    
    /**
     * 根据用户名查询用户
     * 
     * @param username 用户名
     * @return 用户信息
     */
    User selectUserByUsername(String username);
    
    /**
     * 查询所有用户
     * 
     * @return 用户列表
     */
    List<User> selectAll();
    
    /**
     * 新增用户
     * 
     * @param user 用户信息
     * @return 结果
     */
    int insert(User user);
    
    /**
     * 修改用户
     * 
     * @param user 用户信息
     * @return 结果
     */
    int update(User user);
    
    /**
     * 删除用户
     * 
     * @param id 用户ID
     * @return 结果
     */
    int deleteById(Integer id);
} 