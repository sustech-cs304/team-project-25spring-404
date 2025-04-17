package com.training.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.training.common.Result;
import com.training.model.Reminder;
import com.training.service.ReminderService;

/**
 * 提醒控制器
 */
@RestController
@RequestMapping("/reminder")
public class ReminderController {
    
    @Autowired
    private ReminderService reminderService;
    
    /**
     * 获取提醒列表
     */
    @GetMapping("/list")
    public Result<List<Reminder>> list() {
        List<Reminder> list = reminderService.selectByUserId(1); // 临时使用固定用户ID
        return Result.success(list);
    }
    
    /**
     * 获取提醒详情
     */
    @GetMapping("/{id}")
    public Result<Reminder> detail(@PathVariable Integer id) {
        Reminder reminder = reminderService.selectById(id);
        return Result.success(reminder);
    }
    
    /**
     * 新增提醒
     */
    @PostMapping("/add")
    public Result<?> add(@RequestBody Reminder reminder) {
        reminder.setUserId(1); // 临时使用固定用户ID
        reminderService.insert(reminder);
        return Result.success();
    }
    
    /**
     * 修改提醒
     */
    @PutMapping
    public Result<?> update(@RequestBody Reminder reminder) {
        reminder.setUserId(1); // 临时使用固定用户ID
        reminderService.update(reminder);
        return Result.success();
    }
    
    /**
     * 删除提醒
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        reminderService.deleteById(id);
        return Result.success();
    }
} 