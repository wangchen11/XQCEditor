/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50515
Source Host           : localhost:3306
Source Database       : sample

Target Server Type    : MYSQL
Target Server Version : 50515
File Encoding         : 65001

Date: 2013-09-23 11:44:01
*/
/*
DROP DATABASE activate;
CREATE DATABASE activate;
USE activate;
SOURCE D://DevelopAndroid/workspace/Activate/doc/sample.sql;
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for product
-- ----------------------------
DROP TABLE IF EXISTS `activated`;
CREATE TABLE `activate` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_id` varchar(100) NOT NULL,
  `app_id` varchar(100) NOT NULL,
  `user_id` varchar(100) NOT NULL,
  `pay_type` varchar(100),
  `result_code` varchar(100),
  `result_string` varchar(100),
  `trade_id` varchar(100),
  `amount` varchar(100),
  `pay_time` datetime,
  `activate_time` datetime,
  `sign` varchar(100),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
