/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80018
 Source Host           : localhost:3306
 Source Schema         : ljj_crawler_v1

 Target Server Type    : MySQL
 Target Server Version : 80018
 File Encoding         : 65001

 Date: 27/07/2020 17:18:27
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for extract_info
-- ----------------------------
DROP TABLE IF EXISTS `extract_info`;
CREATE TABLE `extract_info`  (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `tid` int(11) NOT NULL COMMENT '任务id',
  `pid` int(11) NULL DEFAULT NULL COMMENT '父解析的主键id',
  `content` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '本次解析需要解析的内容,',
  `contentType` int(11) NULL DEFAULT NULL COMMENT '本次解析内容的类型 1：html，2：json,3:link,4:static,5:base64图片信息',
  `selector` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '本次解析的参数信息',
  `resultType` int(11) NULL DEFAULT NULL COMMENT '本次解析结果的类型 1：string，2：array',
  `mount` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '本次解析结果的数据存储挂载点',
  `arrayRange` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '数组的选择范围',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of extract_info
-- ----------------------------
INSERT INTO `extract_info` VALUES (1, 1, NULL, 'http://www.czccb.cn/news/gywm/index.html', 3, NULL, NULL, NULL, NULL);
INSERT INTO `extract_info` VALUES (2, 1, 1, NULL, NULL, '.yw-content_zw', NULL, 'bank_base_info.traceId.intro', NULL);
INSERT INTO `extract_info` VALUES (3, 1, NULL, 'http://www.czccb.cn/templets/czbank/images/logo.gif', 3, NULL, NULL, NULL, NULL);
INSERT INTO `extract_info` VALUES (4, 1, 3, NULL, 5, NULL, NULL, 'bank_base_info.traceId.logo', NULL);
INSERT INTO `extract_info` VALUES (5, 1, NULL, 'http://www.czccb.cn/', 4, NULL, NULL, 'bank_base_info.traceId.siteUrl', NULL);

-- ----------------------------
-- Table structure for task_info
-- ----------------------------
DROP TABLE IF EXISTS `task_info`;
CREATE TABLE `task_info`  (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '爬虫任务名称',
  `startUrl` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `comment` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'comment',
  `state` int(11) NULL DEFAULT NULL COMMENT '状态信息',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of task_info
-- ----------------------------
INSERT INTO `task_info` VALUES (1, '沧州银行基础信息抓取', NULL, NULL, NULL);

-- ----------------------------
-- Table structure for task_rule
-- ----------------------------
DROP TABLE IF EXISTS `task_rule`;
CREATE TABLE `task_rule`  (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `tid` int(11) NOT NULL COMMENT '任务id',
  `field` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '有规则的字段',
  `ruleType` int(255) NULL DEFAULT NULL COMMENT '规则类型 1：递增',
  `ruleParam` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '用于规则的参数',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of task_rule
-- ----------------------------
INSERT INTO `task_rule` VALUES (1, 2, 'page', 1, '1-20');

SET FOREIGN_KEY_CHECKS = 1;
