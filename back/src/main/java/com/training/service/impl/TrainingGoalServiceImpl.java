package com.training.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.training.mapper.TrainingGoalMapper;
import com.training.model.TrainingGoal;
import com.training.service.TrainingGoalService;

/**
 * 训练目标服务实现类
 */
@Service
public class TrainingGoalServiceImpl implements TrainingGoalService {

    @Autowired
    private TrainingGoalMapper goalMapper;

    @Override
    public TrainingGoal selectById(Integer id) {
        return goalMapper.selectById(id);
    }

    @Override
    public List<TrainingGoal> selectByUserId(Integer userId) {
        return goalMapper.selectByUserId(userId);
    }

    @Override
    public int insert(TrainingGoal goal) {
        return goalMapper.insert(goal);
    }

    @Override
    public int update(TrainingGoal goal) {
        return goalMapper.update(goal);
    }

    @Override
    public int deleteById(Integer id) {
        return goalMapper.deleteById(id);
    }
} 