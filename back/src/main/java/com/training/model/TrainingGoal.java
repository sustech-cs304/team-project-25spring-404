package com.training.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 训练目标实体类
 */
@Data
public class TrainingGoal {
    /** 目标ID */
    private Integer id;
    
    /** 用户ID */
    private Integer userId;
    
    /** 目标名称 */
    private String goalName;
    
    /** 目标描述 */
    private String goalDesc;
    
    /** 目标值 */
    private Double targetValue;
    
    /** 当前值 */
    private Double currentValue;
    
    /** 单位 */
    private String unit;
    
    /** 开始日期 */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startDate;
    
    /** 结束日期 */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endDate;
    
    /** 状态：0-进行中，1-已完成，2-已放弃 */
    private Integer status;
    
    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    
    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
} 