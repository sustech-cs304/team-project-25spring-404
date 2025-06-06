package com.training.model;

import java.util.Date;

import lombok.Data;

/**
 * 用户实体类
 */
@Data
public class User {
    /** 用户ID */
    private Integer id;
    
    /** 用户名 */
    private String username;
    
    /** 密码 */
    private String password;
    
    /** 邮箱 */
    private String email;
    
    /** 昵称 */
    private String nickname;
    
    /** 头像 */
    private String avatar;
    
    /** 创建时间 */
    private Date createTime;
    
    /** 更新时间 */
    private Date updateTime;
} 