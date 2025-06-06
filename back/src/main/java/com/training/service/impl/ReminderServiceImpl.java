package com.training.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.training.mapper.ReminderMapper;
import com.training.model.Reminder;
import com.training.service.ReminderService;

/**
 * 提醒服务实现类
 */
@Service
public class ReminderServiceImpl implements ReminderService {

    @Autowired
    private ReminderMapper reminderMapper;

    @Override
    public Reminder selectById(Integer id) {
        return reminderMapper.selectById(id);
    }

    @Override
    public List<Reminder> selectByUserId(Integer userId) {
        return reminderMapper.selectByUserId(userId);
    }

    @Override
    public int insert(Reminder reminder) {
        return reminderMapper.insert(reminder);
    }

    @Override
    public int update(Reminder reminder) {
        return reminderMapper.update(reminder);
    }

    @Override
    public int deleteById(Integer id) {
        return reminderMapper.deleteById(id);
    }
} 