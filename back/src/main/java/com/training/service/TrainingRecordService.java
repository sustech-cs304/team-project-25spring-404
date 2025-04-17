package com.training.service;

import java.util.List;

import com.training.model.TrainingRecord;

/**
 * 训练记录服务接口
 */
public interface TrainingRecordService {
    
    /**
     * 根据ID查询记录
     */
    TrainingRecord selectById(Integer id);
    
    /**
     * 根据用户ID查询记录列表
     */
    List<TrainingRecord> selectByUserId(Integer userId);
    
    /**
     * 根据任务ID查询记录列表
     */
    List<TrainingRecord> selectByTaskId(Integer taskId);
    
    /**
     * 根据目标ID查询记录列表
     */
    List<TrainingRecord> selectByGoalId(Integer goalId);
    
    /**
     * 新增记录
     */
    int insert(TrainingRecord record);
    
    /**
     * 修改记录
     */
    int update(TrainingRecord record);
    
    /**
     * 删除记录
     */
    int deleteById(Integer id);
} 