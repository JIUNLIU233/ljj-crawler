/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80018
 Source Host           : localhost:3306
 Source Schema         : ljj_crawler

 Target Server Type    : MySQL
 Target Server Version : 80018
 File Encoding         : 65001

 Date: 13/07/2020 16:06:06
*/

SET NAMES utf8;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for extract_info
-- ----------------------------
DROP TABLE IF EXISTS `extract_info`;
CREATE TABLE `extract_info`  (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `task_id` int(11) NOT NULL COMMENT '解析所属任务的主键id',
  `parent_id` int(11) NULL DEFAULT NULL COMMENT '如果为子解析时，其父解析的主键id信息',
  `field_name` varchar(255) NULL DEFAULT NULL COMMENT '解析出的信息的字段名称',
  `extract_type` int(11) NOT NULL COMMENT '解析方式 0：css，1：xpath，2：正则，3：js',
  `extract_attr` varchar(255) NULL DEFAULT NULL COMMENT '选择属性时设置的值。',
  `extract_param` varchar(255) NOT NULL COMMENT '提供给解析器的解析参数',
  `result_type` int(255) NULL DEFAULT NULL COMMENT '解析返回类型 0：string，1：array，2：node',
  `save_type` varchar(255) NULL DEFAULT NULL COMMENT '保存方式',
  `have_child` int(11) NULL DEFAULT NULL COMMENT '是否包含子解析 0：不包含，1：包含',
  `array_range` varchar(255) NULL DEFAULT NULL COMMENT '如果是array的时候，设置舍弃的行数eg：3-8，代表第一行第二行不要了，最后的8行不要了',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of extract_info
-- ----------------------------
INSERT INTO `extract_info` VALUES (1, 1, NULL, 'sites', 0, NULL, 'body > div.content > div.erjiright > div.ejri_r_content > div.yw-content_zw > table > tbody > tr', 1, NULL, 1, '1-0');
INSERT INTO `extract_info` VALUES (2, 1, 1, 'siteName', 0, NULL, 'td:nth-child(1)', 0, NULL, 0, NULL);
INSERT INTO `extract_info` VALUES (3, 1, 1, 'siteAddr', 0, NULL, 'td:nth-child(2)', 0, NULL, 0, NULL);
INSERT INTO `extract_info` VALUES (4, 1, 1, 'sitePhone', 0, NULL, 'td:nth-child(3)', 0, NULL, 0, NULL);
INSERT INTO `extract_info` VALUES (5, 2, NULL, 'booklist', 0, NULL, '#newscontent > div.l > ul > li', 1, NULL, 1, NULL);
INSERT INTO `extract_info` VALUES (6, 2, 5, 'bookName', 0, NULL, 'span.s2 > a', 0, NULL, 0, NULL);
INSERT INTO `extract_info` VALUES (7, 2, 5, 'bookUrl', 0, 'href', 'span.s2 > a', 0, NULL, NULL, NULL);
INSERT INTO `extract_info` VALUES (8, 2, 5, 'latestChapter', 0, NULL, 'span.s3 > a', 0, NULL, NULL, NULL);
INSERT INTO `extract_info` VALUES (9, 2, 5, 'latestChapterUrl', 0, 'href', 'span.s3 > a', 0, NULL, NULL, NULL);
INSERT INTO `extract_info` VALUES (10, 2, 5, 'author', 0, NULL, 'span.s5', 0, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for task_info
-- ----------------------------
DROP TABLE IF EXISTS `task_info`;
CREATE TABLE `task_info`  (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `name` varchar(255)  NULL DEFAULT NULL COMMENT '爬虫任务名称',
  `start_url` varchar(255)  NULL DEFAULT NULL COMMENT '爬虫任务起始url',
  `have_rule` int(11) NULL DEFAULT NULL COMMENT 'starturl 是否含有规则',
  `status` int(11) NULL DEFAULT NULL COMMENT '任务运行状态',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of task_info
-- ----------------------------
INSERT INTO `task_info` VALUES (1, '沧州银行 网点信息抓取', 'http://www.czccb.cn/news/2010927/n7847650.html', 0, 0);
INSERT INTO `task_info` VALUES (2, '新笔趣阁 玄幻小说抓取', 'http://www.xbiquge.la/fenlei/1_{page}.html', 1, 0);

-- ----------------------------
-- Table structure for task_rule
-- ----------------------------
DROP TABLE IF EXISTS `task_rule`;
CREATE TABLE `task_rule`  (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `task_id` int(11) NOT NULL COMMENT '对应所属任务的主键id',
  `param_name` varchar(255)  NULL DEFAULT NULL COMMENT '对应url中规则部分的参数名称',
  `rule_type` int(11) NULL DEFAULT NULL COMMENT '对应param_name的规则类型，0：递增',
  `rule_param` varchar(255)  NULL DEFAULT NULL COMMENT '用于生成规则的参数',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of task_rule
-- ----------------------------
INSERT INTO `task_rule` VALUES (1, 2, 'page', 0, '1-2');

SET FOREIGN_KEY_CHECKS = 1;
