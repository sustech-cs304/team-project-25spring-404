package com.training.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 训练记录实体类
 */
@Data
public class TrainingRecord {
    /** 记录ID */
    private Integer id;
    
    /** 用户ID */
    private Integer userId;
    
    /** 任务ID */
    private Integer taskId;
    
    /** 目标ID */
    private Integer goalId;
    
    /** 目标名称 - 非数据库字段 */
    private transient String goalName;
    
    /** 单位 - 非数据库字段 */
    private transient String unit;
    
    /** 记录值 */
    private Double recordValue;
    
    /** 记录描述 */
    private String recordDesc;
    
    /** 记录备注 */
    private String recordRemark;
    
    /** 开始日期 */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startDate;
    
    /** 记录日期 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date recordDate;
    
    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    
    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
} 