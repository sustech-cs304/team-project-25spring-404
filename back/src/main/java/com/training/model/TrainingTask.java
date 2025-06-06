package com.training.model;

import java.util.Date;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/**
 * 训练任务实体类
 */
@Data
public class TrainingTask {
    /** 任务ID */
    private Integer id;
    
    /** 用户ID */
    private Integer userId;
    
    /** 任务名称 */
    @NotEmpty(message = "任务名称不能为空")
    private String taskName;
    
    /** 任务描述 */
    private String taskDesc;
    
    /** 开始时间 */
    @NotNull(message = "开始时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;
    
    /** 结束时间 */
    @NotNull(message = "结束时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;
    
    /** 重复类型：0-不重复，1-每天，2-每周 */
    private Integer repeatType;
    
    /** 重复天数，用逗号分隔，如1,2,3表示周一、周二、周三 */
    private String repeatDays;
    
    /** 状态：0-未开始，1-进行中，2-已完成，3-已取消 */
    private Integer status;
    
    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    
    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
} 