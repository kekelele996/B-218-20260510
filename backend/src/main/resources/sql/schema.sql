-- ============================================
-- 宠物领养系统数据库初始化脚本
-- Database: pet_adoption
-- ============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `pet_adoption`
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE `pet_adoption`;

-- ============================================
-- 1. 用户表
-- ============================================
DROP TABLE IF EXISTS `adoption_application`;
DROP TABLE IF EXISTS `pet`;
DROP TABLE IF EXISTS `breed_dict`;
DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(100) NOT NULL COMMENT '密码(BCrypt加密)',
  `phone` VARCHAR(20) NOT NULL COMMENT '手机号',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `real_name` VARCHAR(50) NOT NULL COMMENT '真实姓名',
  `avatar` VARCHAR(255) DEFAULT '/images/avatars/default.png' COMMENT '头像URL',
  `address` VARCHAR(255) DEFAULT NULL COMMENT '联系地址',
  `status` TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态: 1正常 0禁用',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_phone` (`phone`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ============================================
-- 2. 品种字典表
-- ============================================
CREATE TABLE `breed_dict` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '品种ID',
  `pet_type` VARCHAR(20) NOT NULL COMMENT '宠物类型: 猫/狗/其他',
  `breed_name` VARCHAR(50) NOT NULL COMMENT '品种名称',
  `sort_order` INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '排序权重',
  PRIMARY KEY (`id`),
  KEY `idx_pet_type` (`pet_type`),
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='品种字典表';

-- ============================================
-- 3. 宠物表
-- ============================================
CREATE TABLE `pet` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '宠物ID',
  `name` VARCHAR(50) NOT NULL COMMENT '宠物名称',
  `type` VARCHAR(20) NOT NULL COMMENT '宠物类型: 猫/狗/其他',
  `breed` VARCHAR(50) DEFAULT NULL COMMENT '品种',
  `age` INT UNSIGNED DEFAULT NULL COMMENT '年龄(月)',
  `gender` VARCHAR(10) DEFAULT NULL COMMENT '性别: 公/母',
  `size` VARCHAR(20) DEFAULT NULL COMMENT '体型: 小型/中型/大型',
  `color` VARCHAR(50) DEFAULT NULL COMMENT '毛色',
  `weight` DECIMAL(5,2) UNSIGNED DEFAULT NULL COMMENT '体重(kg)',
  `vaccinated` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否接种疫苗: 1是 0否',
  `neutered` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否绝育: 1是 0否',
  `health_desc` TEXT DEFAULT NULL COMMENT '健康描述',
  `personality` TEXT DEFAULT NULL COMMENT '性格介绍',
  `requirement` TEXT DEFAULT NULL COMMENT '领养要求',
  `images` TEXT DEFAULT NULL COMMENT '图片URL(逗号分隔)',
  `status` TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态: 1可领养 2已领养 0下架',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_type` (`type`),
  KEY `idx_breed` (`breed`),
  KEY `idx_status` (`status`),
  KEY `idx_gender` (`gender`),
  KEY `idx_size` (`size`),
  KEY `idx_age` (`age`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='宠物表';

-- ============================================
-- 4. 领养申请表
-- ============================================
CREATE TABLE `adoption_application` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '申请ID',
  `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
  `pet_id` BIGINT UNSIGNED NOT NULL COMMENT '宠物ID',
  `living_type` VARCHAR(20) DEFAULT NULL COMMENT '居住类型: 独居/与家人同住/合租',
  `housing_type` VARCHAR(20) DEFAULT NULL COMMENT '住房类型: 自有房产/租房',
  `has_yard` TINYINT UNSIGNED DEFAULT NULL COMMENT '是否有院子: 1是 0否',
  `experience` VARCHAR(20) DEFAULT NULL COMMENT '养宠经验: 无/1-3年/3年以上',
  `current_pets` VARCHAR(100) DEFAULT NULL COMMENT '现有宠物(逗号分隔)',
  `daily_time` VARCHAR(20) DEFAULT NULL COMMENT '每日陪伴时间: <2小时/2-5小时/>5小时',
  `reason` TEXT DEFAULT NULL COMMENT '领养理由',
  `contact_address` VARCHAR(255) DEFAULT NULL COMMENT '联系地址',
  `status` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '状态: 0待审核 1审核中 2通过 3拒绝 4取消',
  `remark` TEXT DEFAULT NULL COMMENT '审核备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_pet_id` (`pet_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_adoption_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_adoption_pet` FOREIGN KEY (`pet_id`) REFERENCES `pet` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='领养申请表';
