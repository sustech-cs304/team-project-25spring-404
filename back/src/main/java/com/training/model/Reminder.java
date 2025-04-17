package com.training.model;

import java.util.Date;

import lombok.Data;

/**
 * 提醒实体类
 */
@Data
public class Reminder {
    /** 提醒ID */
    private Integer id;
    
    /** 用户ID */
    private Integer userId;
    
    /** 提醒标题 */
    private String title;
    
    /** 提醒内容 */
    private String content;
    
    /** 提醒时间 */
    private Date remindTime;
    
    /** 重复类型：0-不重复，1-每天，2-每周，3-每月 */
    private Integer repeatType;
    
    /** 状态：0-未完成，1-已完成 */
    private Integer status;
    
    /** 创建时间 */
    private Date createTime;
    
    /** 更新时间 */
    private Date updateTime;
} 