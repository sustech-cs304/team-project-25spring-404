-- 创建数据库
DROP DATABASE IF EXISTS training_management;
CREATE DATABASE training_management;
USE training_management;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 删除训练任务表（如果存在）
DROP TABLE IF EXISTS `training_task`;

-- 创建训练任务表
CREATE TABLE `training_task` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `task_name` varchar(50) NOT NULL COMMENT '任务名称',
  `task_desc` varchar(500) DEFAULT NULL COMMENT '任务描述',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `repeat_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '重复类型：0-不重复，1-每天，2-每周，3-每月',
  `repeat_days` varchar(50) DEFAULT NULL COMMENT '重复日期，用逗号分隔，如：1,3,5',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态：0-未开始，1-进行中，2-已完成，3-已取消',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  CONSTRAINT `fk_task_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='训练任务表';

-- 训练目标表
CREATE TABLE IF NOT EXISTS `training_goal` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `goal_name` varchar(100) NOT NULL COMMENT '目标名称',
  `goal_desc` varchar(500) DEFAULT NULL COMMENT '目标描述',
  `target_value` double DEFAULT NULL COMMENT '目标值',
  `current_value` double DEFAULT '0' COMMENT '当前值',
  `unit` varchar(20) DEFAULT NULL COMMENT '单位',
  `start_date` date NOT NULL COMMENT '开始日期',
  `end_date` date DEFAULT NULL COMMENT '结束日期',
  `status` tinyint(4) DEFAULT '0' COMMENT '状态：0-进行中，1-已完成，2-已放弃',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `IX_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='训练目标表';

-- 训练记录表
CREATE TABLE IF NOT EXISTS `training_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `task_id` int(11) DEFAULT NULL COMMENT '任务ID',
  `goal_id` int(11) DEFAULT NULL COMMENT '目标ID',
  `record_value` double DEFAULT NULL COMMENT '记录值',
  `record_desc` varchar(500) DEFAULT NULL COMMENT '记录描述',
  `record_remark` varchar(500) DEFAULT NULL COMMENT '记录备注',
  `start_date` date NOT NULL COMMENT '开始日期',
  `record_date` date NOT NULL COMMENT '记录日期',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `IX_user_id` (`user_id`),
  KEY `IX_task_id` (`task_id`),
  KEY `IX_goal_id` (`goal_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='训练记录表';

-- 删除已存在的表（如果存在）
DROP TABLE IF EXISTS `reminder`;

-- 创建提醒表
CREATE TABLE `reminder` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '提醒ID',
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `remind_title` varchar(50) NOT NULL COMMENT '提醒标题',
  `remind_content` varchar(500) DEFAULT NULL COMMENT '提醒内容',
  `remind_time` datetime NOT NULL COMMENT '提醒时间',
  `repeat_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '重复类型（0不重复 1每天 2每周 3每月）',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态（0未完成 1已完成）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_remind_time` (`remind_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='提醒表';

-- 插入默认数据
-- 用户
INSERT INTO `user` (`username`, `password`, `email`, `nickname`, `avatar`) VALUES
('admin', '21232f297a57a5a743894a0e4a801fc3', 'admin@example.com', '管理员', 'default.jpg'),
('test', '098f6bcd4621d373cade4e832627b4f6', 'test@example.com', '测试用户', 'default.jpg');

-- 训练任务
INSERT INTO `training_task` (`user_id`, `task_name`, `task_desc`, `start_time`, `end_time`, `repeat_type`, `repeat_days`, `status`) VALUES
(1, '晨跑', '每天早上6点30分跑步30分钟', '2023-05-01 06:30:00', '2023-05-01 07:00:00', 1, NULL, 1),
(1, '力量训练', '每周一三五进行力量训练', '2023-05-01 18:00:00', '2023-05-01 19:00:00', 2, '1,3,5', 0),
(2, '瑜伽', '每天晚上放松瑜伽', '2023-05-01 21:00:00', '2023-05-01 21:30:00', 1, NULL, 1);

-- 训练目标
INSERT INTO `training_goal` (`user_id`, `goal_name`, `goal_desc`, `target_value`, `current_value`, `unit`, `start_date`, `end_date`, `status`) VALUES
(1, '减肥目标', '3个月减重10公斤', 10, 3, '公斤', '2023-05-01', '2023-08-01', 0),
(1, '马拉松训练', '为年底马拉松做准备', 42.195, 20, '公里', '2023-05-01', '2023-12-01', 0),
(2, '增肌计划', '增加肌肉量', 5, 2, '公斤', '2023-05-01', '2023-07-31', 0);

-- 训练记录
INSERT INTO `training_record` (`user_id`, `task_id`, `goal_id`, `record_value`, `record_desc`, `record_remark`, `start_date`, `record_date`) VALUES
(1, 1, 1, 1, '今天完成了30分钟晨跑，感觉不错', '天气晴朗，状态良好', '2023-05-01', '2023-05-01'),
(1, 2, NULL, NULL, '完成了力量训练，主要是上肢', '肱二头肌有些酸痛', '2023-05-01', '2023-05-01'),
(2, 3, 3, 0.5, '完成了瑜伽练习，体重减轻0.5公斤', '需要加强柔韧性训练', '2023-05-01', '2023-05-01');

-- 提醒设置
INSERT INTO `reminder` (`user_id`, `remind_title`, `remind_content`, `remind_time`, `repeat_type`, `status`) VALUES
(1, '晨跑提醒', '别忘了晨跑哦！', '2023-05-02 06:00:00', 1, 0),
(1, '力量训练提醒', '今天的力量训练别忘了', '2023-05-03 17:30:00', 2, 0),
(2, '瑜伽提醒', '瑜伽时间快到了', '2023-05-02 20:30:00', 1, 0); 