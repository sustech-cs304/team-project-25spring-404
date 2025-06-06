package com.training.mapper;

import java.util.List;

import com.training.model.Reminder;

/**
 * 提醒Mapper接口
 */
public interface ReminderMapper {
    
    /**
     * 根据ID查询提醒
     */
    Reminder selectById(Integer id);
    
    /**
     * 根据用户ID查询提醒列表
     */
    List<Reminder> selectByUserId(Integer userId);
    
    /**
     * 新增提醒
     */
    int insert(Reminder reminder);
    
    /**
     * 修改提醒
     */
    int update(Reminder reminder);
    
    /**
     * 删除提醒
     */
    int deleteById(Integer id);
} 