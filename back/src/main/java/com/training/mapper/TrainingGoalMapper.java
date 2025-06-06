package com.training.mapper;

import java.util.List;

import com.training.model.TrainingGoal;

/**
 * 训练目标Mapper接口
 */
public interface TrainingGoalMapper {
    
    /**
     * 根据目标ID查询目标
     * 
     * @param id 目标ID
     * @return 目标信息
     */
    TrainingGoal selectById(Integer id);
    
    /**
     * 根据用户ID查询目标列表
     * 
     * @param userId 用户ID
     * @return 目标列表
     */
    List<TrainingGoal> selectByUserId(Integer userId);
    
    /**
     * 新增目标
     * 
     * @param goal 目标信息
     * @return 结果
     */
    int insert(TrainingGoal goal);
    
    /**
     * 修改目标
     * 
     * @param goal 目标信息
     * @return 结果
     */
    int update(TrainingGoal goal);
    
    /**
     * 删除目标
     * 
     * @param id 目标ID
     * @return 结果
     */
    int deleteById(Integer id);
} 