package com.training.mapper;

import java.util.List;

import com.training.model.TrainingRecord;

/**
 * 训练记录Mapper接口
 */
public interface TrainingRecordMapper {
    
    /**
     * 根据记录ID查询记录
     * 
     * @param id 记录ID
     * @return 记录信息
     */
    TrainingRecord selectById(Integer id);
    
    /**
     * 根据用户ID查询记录列表
     * 
     * @param userId 用户ID
     * @return 记录列表
     */
    List<TrainingRecord> selectByUserId(Integer userId);
    
    /**
     * 根据任务ID查询记录列表
     * 
     * @param taskId 任务ID
     * @return 记录列表
     */
    List<TrainingRecord> selectByTaskId(Integer taskId);
    
    /**
     * 根据目标ID查询记录列表
     * 
     * @param goalId 目标ID
     * @return 记录列表
     */
    List<TrainingRecord> selectByGoalId(Integer goalId);
    
    /**
     * 新增记录
     * 
     * @param record 记录信息
     * @return 结果
     */
    int insert(TrainingRecord record);
    
    /**
     * 修改记录
     * 
     * @param record 记录信息
     * @return 结果
     */
    int update(TrainingRecord record);
    
    /**
     * 删除记录
     * 
     * @param id 记录ID
     * @return 结果
     */
    int deleteById(Integer id);
} 