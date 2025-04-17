package com.training.service.impl;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.training.mapper.TrainingRecordMapper;
import com.training.model.TrainingGoal;
import com.training.model.TrainingRecord;
import com.training.service.TrainingGoalService;
import com.training.service.TrainingRecordService;

/**
 * 训练记录服务实现类
 */
@Service
public class TrainingRecordServiceImpl implements TrainingRecordService {

    @Autowired
    private TrainingRecordMapper recordMapper;
    
    @Autowired
    private TrainingGoalService goalService;

    @Override
    public TrainingRecord selectById(Integer id) {
        TrainingRecord record = recordMapper.selectById(id);
        if (record != null && record.getGoalId() != null) {
            fillGoalInfo(record);
        }
        return record;
    }

    @Override
    public List<TrainingRecord> selectByUserId(Integer userId) {
        List<TrainingRecord> records = recordMapper.selectByUserId(userId);
        fillGoalInfoBatch(records);
        return records;
    }

    @Override
    public List<TrainingRecord> selectByTaskId(Integer taskId) {
        List<TrainingRecord> records = recordMapper.selectByTaskId(taskId);
        fillGoalInfoBatch(records);
        return records;
    }

    @Override
    public List<TrainingRecord> selectByGoalId(Integer goalId) {
        List<TrainingRecord> records = recordMapper.selectByGoalId(goalId);
        TrainingGoal goal = goalService.selectById(goalId);
        if (goal != null) {
            for (TrainingRecord record : records) {
                record.setGoalName(goal.getGoalName());
                record.setUnit(goal.getUnit());
            }
        }
        return records;
    }

    @Override
    public int insert(TrainingRecord record) {
        return recordMapper.insert(record);
    }

    @Override
    public int update(TrainingRecord record) {
        return recordMapper.update(record);
    }

    @Override
    public int deleteById(Integer id) {
        return recordMapper.deleteById(id);
    }
    
    /**
     * 填充单个记录的目标信息
     */
    private void fillGoalInfo(TrainingRecord record) {
        if (record.getGoalId() != null) {
            TrainingGoal goal = goalService.selectById(record.getGoalId());
            if (goal != null) {
                record.setGoalName(goal.getGoalName());
                record.setUnit(goal.getUnit());
            }
        }
    }
    
    /**
     * 批量填充记录的目标信息
     */
    private void fillGoalInfoBatch(List<TrainingRecord> records) {
        if (records == null || records.isEmpty()) {
            return;
        }
        
        // 提取所有不为空的目标ID
        List<Integer> goalIds = records.stream()
                .filter(r -> r.getGoalId() != null)
                .map(TrainingRecord::getGoalId)
                .distinct()
                .collect(Collectors.toList());
        
        if (!goalIds.isEmpty()) {
            // 一次性查询所有目标
            Map<Integer, TrainingGoal> goalMap = goalIds.stream()
                    .map(id -> goalService.selectById(id))
                    .filter(goal -> goal != null)
                    .collect(Collectors.toMap(TrainingGoal::getId, Function.identity()));
            
            // 填充目标信息
            for (TrainingRecord record : records) {
                if (record.getGoalId() != null) {
                    TrainingGoal goal = goalMap.get(record.getGoalId());
                    if (goal != null) {
                        record.setGoalName(goal.getGoalName());
                        record.setUnit(goal.getUnit());
                    }
                }
            }
        }
    }
} 