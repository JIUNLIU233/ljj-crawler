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

 Date: 17/08/2020 21:09:20
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
  `selectorAttr` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '解析html中的属性内容',
  `resultType` int(11) NULL DEFAULT NULL COMMENT '本次解析结果的类型 1：string，2：array',
  `mount` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '本次解析结果的数据存储挂载点',
  `arrayRange` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '数组的选择范围',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 41 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of extract_info
-- ----------------------------
INSERT INTO `extract_info` VALUES (1, 1, NULL, NULL, NULL, '#info > h1', NULL, NULL, 'xbqg.bookName', NULL);
INSERT INTO `extract_info` VALUES (2, 1, NULL, NULL, NULL, '#info > p:nth-child(2)', NULL, NULL, 'xbqg.bookAuthor', NULL);
INSERT INTO `extract_info` VALUES (3, 1, NULL, NULL, NULL, '#info > p:nth-child(4)', NULL, NULL, 'xbqg.lastUpdateTime', NULL);
INSERT INTO `extract_info` VALUES (4, 1, NULL, NULL, NULL, '#intro > p:nth-child(2)', NULL, NULL, 'xbqg.bookIntro', NULL);
INSERT INTO `extract_info` VALUES (22, 4, NULL, '', NULL, '#info > h1', NULL, NULL, 'xbqg_base.bookName', NULL);
INSERT INTO `extract_info` VALUES (23, 4, NULL, NULL, NULL, '#info > p:nth-child(2)', NULL, NULL, 'xbqg_base.bookAuthor', NULL);
INSERT INTO `extract_info` VALUES (24, 4, NULL, NULL, NULL, '#info > p:nth-child(4)', NULL, NULL, 'xbqg_base.latestUpdateTme', NULL);
INSERT INTO `extract_info` VALUES (25, 4, NULL, NULL, NULL, '#info > p:nth-child(5) > a', NULL, NULL, 'xbqg_base.latestChapterName', NULL);
INSERT INTO `extract_info` VALUES (26, 4, NULL, NULL, NULL, '#intro > p:nth-child(2)', NULL, NULL, 'xbqg_base.bookAuthor', NULL);
INSERT INTO `extract_info` VALUES (27, 4, NULL, NULL, NULL, '#info > p:nth-child(5) > a', 'href', NULL, 'xbqg_base.latestChapterUrl', NULL);
INSERT INTO `extract_info` VALUES (28, 4, NULL, NULL, NULL, '#list > dl > dd', NULL, 2, NULL, '1-1050');
INSERT INTO `extract_info` VALUES (29, 4, 28, NULL, NULL, 'a', NULL, NULL, 'xbqg_chapter[new].chapterName', NULL);
INSERT INTO `extract_info` VALUES (30, 4, 28, NULL, NULL, 'a', 'href', NULL, 'xbqg_chapter[new].chapterUrl', NULL);
INSERT INTO `extract_info` VALUES (31, 5, NULL, NULL, NULL, '#info > h1', NULL, NULL, 'sdb.bookName', NULL);
INSERT INTO `extract_info` VALUES (32, 5, NULL, NULL, NULL, '#intro > p:nth-child(2)', NULL, NULL, 'sdb.bookIntro', NULL);
INSERT INTO `extract_info` VALUES (33, 5, NULL, NULL, NULL, '#list > dl > dd', NULL, 2, NULL, NULL);
INSERT INTO `extract_info` VALUES (34, 5, 33, NULL, NULL, 'a', NULL, NULL, 'ljj[new].chapterName', NULL);
INSERT INTO `extract_info` VALUES (35, 5, 33, NULL, NULL, 'a', 'href', NULL, 'ljj[new].chapterUrl', NULL);
INSERT INTO `extract_info` VALUES (36, 5, 33, NULL, NULL, 'a', 'href', 3, NULL, NULL);
INSERT INTO `extract_info` VALUES (37, 5, 36, NULL, NULL, '#content', NULL, NULL, 'ljj.chapterContent', NULL);
INSERT INTO `extract_info` VALUES (38, 6, NULL, NULL, NULL, 'div[itemprop=articleBody] a', NULL, 2, NULL, NULL);
INSERT INTO `extract_info` VALUES (39, 6, 38, NULL, NULL, 'a', 'href', 3, NULL, NULL);
INSERT INTO `extract_info` VALUES (40, 6, 39, NULL, 5, NULL, NULL, NULL, 'pic.UUID', NULL);

-- ----------------------------
-- Table structure for extract_result_type
-- ----------------------------
DROP TABLE IF EXISTS `extract_result_type`;
CREATE TABLE `extract_result_type`  (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `type` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `comment` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of extract_result_type
-- ----------------------------
INSERT INTO `extract_result_type` VALUES (1, 'STRING', '解析后的内容为一个字符串');
INSERT INTO `extract_result_type` VALUES (2, 'ARRAY', '解析后返回的内容为一个数组');
INSERT INTO `extract_result_type` VALUES (3, 'LINK', '解析后返回的内容为一个链接');
INSERT INTO `extract_result_type` VALUES (4, 'BASE64', '解析返回的字节码数组为一个base64编码');

-- ----------------------------
-- Table structure for extract_type
-- ----------------------------
DROP TABLE IF EXISTS `extract_type`;
CREATE TABLE `extract_type`  (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `type` varchar(16) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '页面类型',
  `comment` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '关于数据的解释',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `extract_type`(`type`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of extract_type
-- ----------------------------
INSERT INTO `extract_type` VALUES (1, 'HTML', 'response内容为HTML静态页面信息');
INSERT INTO `extract_type` VALUES (2, 'STRING', 'response内容为纯字符串信息，需要单独进行解析');
INSERT INTO `extract_type` VALUES (3, 'JSON', 'response内容为JSON 数据（部分数据有call_back，这里可以考虑call_back的处理方式）');
INSERT INTO `extract_type` VALUES (4, 'FILE', '返回内容为一个文件类型（包含文件夹和文件）');
INSERT INTO `extract_type` VALUES (5, 'STATIC', '传入的固定内容直接入库');
INSERT INTO `extract_type` VALUES (6, 'LINK', '传入的解析param为一个链接');
INSERT INTO `extract_type` VALUES (7, 'JS', '传入内容需要通过js来进行选择');

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
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of task_info
-- ----------------------------
INSERT INTO `task_info` VALUES (1, '新笔趣阁 三寸人间抓取案例', 'http://www.xbiquge.la/10/10489/', '抓取该小说的基本信息', NULL);
INSERT INTO `task_info` VALUES (4, '单本小说抓取', 'http://www.xbiquge.la/10/10489/', '抓取一本小说说有内容的案例', NULL);
INSERT INTO `task_info` VALUES (5, 'a', 'http://www.xbiquge.la/0/951/', '2', NULL);
INSERT INTO `task_info` VALUES (6, '图片抓取案例', 'http://nsfwpicx.com/archives/1904.html', NULL, NULL);

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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of task_rule
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
