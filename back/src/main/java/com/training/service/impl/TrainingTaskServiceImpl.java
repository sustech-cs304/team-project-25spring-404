package com.training.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.training.mapper.TrainingTaskMapper;
import com.training.model.TrainingTask;
import com.training.service.TrainingTaskService;

/**
 * 训练任务服务实现类
 */
@Service
public class TrainingTaskServiceImpl implements TrainingTaskService {

    @Autowired
    private TrainingTaskMapper taskMapper;

    @Override
    public TrainingTask getTaskById(Integer id) {
        return taskMapper.selectById(id);
    }

    @Override
    public List<TrainingTask> getTasksByUserId(Integer userId) {
        return taskMapper.selectByUserId(userId);
    }

    @Override
    public int addTask(TrainingTask task) {
        return taskMapper.insert(task);
    }

    @Override
    public int updateTask(TrainingTask task) {
        return taskMapper.update(task);
    }

    @Override
    public int deleteTask(Integer id) {
        return taskMapper.deleteById(id);
    }
} 