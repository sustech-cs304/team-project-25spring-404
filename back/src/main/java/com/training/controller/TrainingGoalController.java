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
import com.training.model.TrainingGoal;
import com.training.model.TrainingRecord;
import com.training.service.TrainingGoalService;
import com.training.service.TrainingRecordService;

/**
 * 训练目标控制器
 */
@RestController
@RequestMapping("/goal")
public class TrainingGoalController {
    
    @Autowired
    private TrainingGoalService goalService;
    
    @Autowired
    private TrainingRecordService recordService;
    
    /**
     * 获取目标列表
     */
    @GetMapping("/list")
    public Result<List<TrainingGoal>> list() {
        List<TrainingGoal> list = goalService.selectByUserId(1); // 临时使用固定用户ID
        return Result.success(list);
    }
    
    /**
     * 获取目标详情
     */
    @GetMapping("/{id}")
    public Result<TrainingGoal> detail(@PathVariable Integer id) {
        TrainingGoal goal = goalService.selectById(id);
        return Result.success(goal);
    }
    
    /**
     * 获取目标关联的所有记录
     */
    @GetMapping("/record/all")
    public Result<List<TrainingRecord>> getAllRecords() {
        // 临时使用固定用户ID
        List<TrainingRecord> records = recordService.selectByUserId(1);
        return Result.success(records);
    }
    
    /**
     * 获取特定目标的记录
     */
    @GetMapping("/{id}/records")
    public Result<List<TrainingRecord>> getGoalRecords(@PathVariable Integer id) {
        List<TrainingRecord> records = recordService.selectByGoalId(id);
        return Result.success(records);
    }
    
    /**
     * 获取特定目标的记录（另一个URL格式）
     */
    @GetMapping("/record/list/{goalId}")
    public Result<List<TrainingRecord>> getRecordsByGoalId(@PathVariable Integer goalId) {
        List<TrainingRecord> records = recordService.selectByGoalId(goalId);
        return Result.success(records);
    }
    
    /**
     * 新增目标
     */
    @PostMapping("/add")
    public Result<?> add(@RequestBody TrainingGoal goal) {
        try {
            System.out.println("Received goal: " + goal);
            
            if (goal.getGoalName() == null || goal.getGoalName().trim().isEmpty()) {
                return Result.error("目标名称不能为空");
            }
            
            if (goal.getStartDate() == null) {
                return Result.error("开始日期不能为空");
            }
            
            goal.setUserId(1); // 临时使用固定用户ID
            
            // 如果当前值为空，设置为0
            if (goal.getCurrentValue() == null) {
                goal.setCurrentValue(0.0);
            }
            
            // 如果状态为空，设置为0（进行中）
            if (goal.getStatus() == null) {
                goal.setStatus(0);
            }
            
            System.out.println("Processed goal: " + goal);
            
            goalService.insert(goal);
            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("创建训练目标失败: " + e.getMessage());
        }
    }
    
    /**
     * 修改目标
     */
    @PutMapping
    public Result<?> update(@RequestBody TrainingGoal goal) {
        goal.setUserId(1); // 临时使用固定用户ID
        goalService.update(goal);
        return Result.success();
    }
    
    /**
     * 删除目标
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        goalService.deleteById(id);
        return Result.success();
    }
    
    /**
     * 删除记录
     */
    @DeleteMapping("/record/{id}")
    public Result<?> deleteRecord(@PathVariable Integer id) {
        try {
            recordService.deleteById(id);
            return Result.success("删除记录成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 修改记录
     */
    @PutMapping("/record")
    public Result<?> updateRecord(@RequestBody TrainingRecord record) {
        try {
            if (record.getId() == null) {
                return Result.error("记录ID不能为空");
            }
            
            if (record.getStartDate() == null) {
                return Result.error("开始日期不能为空");
            }
            
            if (record.getRecordDate() == null) {
                return Result.error("记录日期不能为空");
            }
            
            record.setUserId(1); // 临时使用固定用户ID
            recordService.update(record);
            return Result.success("修改记录成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("修改记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 新增记录
     */
    @PostMapping("/record/add")
    public Result<?> addRecord(@RequestBody TrainingRecord record) {
        try {
            if (record.getStartDate() == null) {
                return Result.error("开始日期不能为空");
            }
            
            if (record.getRecordDate() == null) {
                return Result.error("记录日期不能为空");
            }
            
            record.setUserId(1); // 临时使用固定用户ID
            recordService.insert(record);
            return Result.success("添加记录成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("添加记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 新增记录 (另一个URL格式)
     */
    @PostMapping("/record")
    public Result<?> addRecordAlternative(@RequestBody TrainingRecord record) {
        try {
            if (record.getStartDate() == null) {
                // 如果开始日期为空，设置为当前日期
                record.setStartDate(new java.util.Date());
            }
            
            if (record.getRecordDate() == null) {
                return Result.error("记录日期不能为空");
            }
            
            record.setUserId(1); // 临时使用固定用户ID
            recordService.insert(record);
            return Result.success("添加记录成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("添加记录失败: " + e.getMessage());
        }
    }
} 