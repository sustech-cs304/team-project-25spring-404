package com.training.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.training.model.TrainingTask;
import com.training.model.User;
import com.training.service.TrainingTaskService;

/**
 * 训练任务控制器
 */
@RestController
@RequestMapping("/task")
public class TrainingTaskController {

    private static final Logger logger = LoggerFactory.getLogger(TrainingTaskController.class);

    @Autowired
    private TrainingTaskService taskService;

    /**
     * 获取当前用户的任务列表
     */
    @GetMapping("/list")
    public Result<List<TrainingTask>> getTaskList(HttpServletRequest request) {
        User user = (User) request.getAttribute("user");
        List<TrainingTask> taskList = taskService.getTasksByUserId(user.getId());
        return Result.success(taskList);
    }

    /**
     * 根据任务ID获取任务详情
     */
    @GetMapping("/{id}")
    public Result<TrainingTask> getTaskById(@PathVariable Integer id, HttpServletRequest request) {
        User user = (User) request.getAttribute("user");
        TrainingTask task = taskService.getTaskById(id);
        if (task != null && task.getUserId().equals(user.getId())) {
            return Result.success(task);
        } else {
            return Result.error("任务不存在或无权限访问");
        }
    }

    /**
     * 新增任务
     */
    @PostMapping("/add")
    public Result<?> addTask(@RequestBody TrainingTask task, HttpServletRequest request) {
        try {
            logger.info("Received task add request: {}", task);
            
            if (task == null) {
                logger.error("Task is null");
                return Result.error("任务数据不能为空");
            }
            
            // 记录所有字段的值，便于调试
            logger.info("Task details - Name: {}, Desc: {}, Start Time: {}, End Time: {}, Repeat Type: {}, Repeat Days: {}, Status: {}",
                    task.getTaskName(), task.getTaskDesc(), task.getStartTime(), task.getEndTime(), 
                    task.getRepeatType(), task.getRepeatDays(), task.getStatus());
            
            if (task.getTaskName() == null || task.getTaskName().trim().isEmpty()) {
                logger.error("Task name is empty");
                return Result.error("任务名称不能为空");
            }
            
            if (task.getStartTime() == null) {
                logger.error("Start time is null");
                return Result.error("开始时间不能为空");
            }
            
            if (task.getEndTime() == null) {
                logger.error("End time is null");
                return Result.error("结束时间不能为空");
            }
            
            // 临时使用固定用户ID
            task.setUserId(1);
            
            // 确保repeatType有默认值
            if (task.getRepeatType() == null) {
                task.setRepeatType(0);
            }
            
            // 确保status有默认值
            if (task.getStatus() == null) {
                task.setStatus(0);
            }
            
            int result = taskService.addTask(task);
            if (result > 0) {
                logger.info("Task added successfully. Task ID: {}", task.getId());
                return Result.success("添加任务成功");
            } else {
                logger.error("Failed to add task");
                return Result.error("添加任务失败");
            }
        } catch (Exception e) {
            logger.error("Exception while adding task", e);
            return Result.error("添加任务失败: " + e.getMessage());
        }
    }

    /**
     * 修改任务
     */
    @PostMapping
    public Result<Void> updateTask(@RequestBody TrainingTask task, HttpServletRequest request) {
        try {
            logger.info("Received task update request: {}", task);
            
            if (task == null || task.getId() == null) {
                logger.error("Task or task ID is null");
                return Result.error("任务ID不能为空");
            }
            
            // 记录所有字段的值，便于调试
            logger.info("Task update details - ID: {}, Name: {}, Desc: {}, Start Time: {}, End Time: {}, Repeat Type: {}, Repeat Days: {}, Status: {}",
                    task.getId(), task.getTaskName(), task.getTaskDesc(), task.getStartTime(), task.getEndTime(), 
                    task.getRepeatType(), task.getRepeatDays(), task.getStatus());
            
            User user = (User) request.getAttribute("user");
            // 确保只能修改自己的任务
            TrainingTask originalTask = taskService.getTaskById(task.getId());
            if (originalTask == null || !originalTask.getUserId().equals(user.getId())) {
                logger.error("Task not found or user not authorized. Task ID: {}, User ID: {}", task.getId(), user.getId());
                return Result.error("任务不存在或无权限修改");
            }
            
            // 设置用户ID，防止篡改
            task.setUserId(user.getId());
            
            // 确保repeatType有默认值
            if (task.getRepeatType() == null) {
                task.setRepeatType(0);
            }
            
            // 确保status有默认值
            if (task.getStatus() == null) {
                task.setStatus(0);
            }
            
            int result = taskService.updateTask(task);
            if (result > 0) {
                logger.info("Task updated successfully. Task ID: {}", task.getId());
                return Result.success("修改任务成功");
            } else {
                logger.error("Failed to update task. Task ID: {}", task.getId());
                return Result.error("修改任务失败");
            }
        } catch (Exception e) {
            logger.error("Exception while updating task", e);
            return Result.error("修改任务失败: " + e.getMessage());
        }
    }

    /**
     * 删除任务
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteTask(@PathVariable Integer id, HttpServletRequest request) {
        User user = (User) request.getAttribute("user");
        // 确保只能删除自己的任务
        TrainingTask task = taskService.getTaskById(id);
        if (task == null || !task.getUserId().equals(user.getId())) {
            return Result.error("任务不存在或无权限删除");
        }
        
        int result = taskService.deleteTask(id);
        if (result > 0) {
            return Result.success("删除任务成功");
        } else {
            return Result.error("删除任务失败");
        }
    }
} 