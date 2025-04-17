package com.training.service;

import java.util.List;

import com.training.model.TrainingTask;

/**
 * 训练任务服务接口
 */
public interface TrainingTaskService {
    
    /**
     * 根据任务ID查询任务
     * 
     * @param id 任务ID
     * @return 任务信息
     */
    TrainingTask getTaskById(Integer id);
    
    /**
     * 根据用户ID查询任务列表
     * 
     * @param userId 用户ID
     * @return 任务列表
     */
    List<TrainingTask> getTasksByUserId(Integer userId);
    
    /**
     * 新增任务
     * 
     * @param task 任务信息
     * @return 结果
     */
    int addTask(TrainingTask task);
    
    /**
     * 修改任务
     * 
     * @param task 任务信息
     * @return 结果
     */
    int updateTask(TrainingTask task);
    
    /**
     * 删除任务
     * 
     * @param id 任务ID
     * @return 结果
     */
    int deleteTask(Integer id);
} 