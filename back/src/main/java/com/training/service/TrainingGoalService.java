package com.training.service;

import java.util.List;

import com.training.model.TrainingGoal;

/**
 * 训练目标服务接口
 */
public interface TrainingGoalService {
    
    /**
     * 根据ID查询目标
     */
    TrainingGoal selectById(Integer id);
    
    /**
     * 根据用户ID查询目标列表
     */
    List<TrainingGoal> selectByUserId(Integer userId);
    
    /**
     * 新增目标
     */
    int insert(TrainingGoal goal);
    
    /**
     * 修改目标
     */
    int update(TrainingGoal goal);
    
    /**
     * 删除目标
     */
    int deleteById(Integer id);
} 