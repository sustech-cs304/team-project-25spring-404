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
import com.training.model.TrainingRecord;
import com.training.service.TrainingRecordService;

/**
 * 训练记录控制器
 */
@RestController
@RequestMapping("/record")
public class TrainingRecordController {
    
    @Autowired
    private TrainingRecordService recordService;
    
    /**
     * 获取所有记录
     */
    @GetMapping("/all")
    public Result<List<TrainingRecord>> getAllRecords() {
        List<TrainingRecord> list = recordService.selectByUserId(1); // 临时使用固定用户ID
        return Result.success(list);
    }
    
    /**
     * 获取记录详情
     */
    @GetMapping("/{id}")
    public Result<TrainingRecord> detail(@PathVariable Integer id) {
        TrainingRecord record = recordService.selectById(id);
        return Result.success(record);
    }
    
    /**
     * 新增记录
     */
    @PostMapping("/add")
    public Result<?> add(@RequestBody TrainingRecord record) {
        try {
            System.out.println("Received record: " + record);
            
            if (record.getStartDate() == null) {
                return Result.error("开始日期不能为空");
            }
            
            if (record.getRecordDate() == null) {
                return Result.error("记录日期不能为空");
            }
            
            record.setUserId(1); // 临时使用固定用户ID
            
            System.out.println("Processed record: " + record);
            
            recordService.insert(record);
            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("创建记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 修改记录
     */
    @PutMapping
    public Result<?> update(@RequestBody TrainingRecord record) {
        try {
            if (record.getStartDate() == null) {
                return Result.error("开始日期不能为空");
            }
            
            if (record.getRecordDate() == null) {
                return Result.error("记录日期不能为空");
            }
            
            record.setUserId(1); // 临时使用固定用户ID
            recordService.update(record);
            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("修改记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除记录
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        recordService.deleteById(id);
        return Result.success();
    }
} 