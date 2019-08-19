-- --------------------------------------------------------
-- 主机:                           10.20.100.70
-- Server version:               5.7.25-0ubuntu0.16.04.2-log - (Ubuntu)
-- Server OS:                    Linux
-- HeidiSQL 版本:                  10.1.0.5464
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


-- Dumping database structure for ZCC_SSO
DROP DATABASE IF EXISTS `ZCC_SSO`;
CREATE DATABASE IF NOT EXISTS `ZCC_SSO` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;
USE `ZCC_SSO`;

-- Dumping structure for table ZCC_SSO.AMC_COMPANY
DROP TABLE IF EXISTS `AMC_COMPANY`;
CREATE TABLE IF NOT EXISTS `AMC_COMPANY` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` char(50) NOT NULL DEFAULT '-1' COMMENT '公司名字',
  `desc` varchar(50) NOT NULL DEFAULT '-1' COMMENT '描述信息',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公司';

-- Data exporting was unselected.
-- Dumping structure for table ZCC_SSO.AMC_DEPT
DROP TABLE IF EXISTS `AMC_DEPT`;
CREATE TABLE IF NOT EXISTS `AMC_DEPT` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `cmpy_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '所属公司id',
  `name` char(50) NOT NULL DEFAULT '-1' COMMENT '部门名字',
  `desc` varchar(50) NOT NULL DEFAULT '-1' COMMENT '描述信息',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门';

-- Data exporting was unselected.
-- Dumping structure for table ZCC_SSO.AMC_PERMISSION
DROP TABLE IF EXISTS `AMC_PERMISSION`;
CREATE TABLE IF NOT EXISTS `AMC_PERMISSION` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4;

-- Data exporting was unselected.
-- Dumping structure for table ZCC_SSO.AMC_PHONE_MSG
DROP TABLE IF EXISTS `AMC_PHONE_MSG`;
CREATE TABLE IF NOT EXISTS `AMC_PHONE_MSG` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `phone_num` char(30) NOT NULL DEFAULT '0',
  `message` varchar(20) NOT NULL DEFAULT '0',
  `check_code` char(10) NOT NULL DEFAULT '0',
  `create_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COMMENT='used for phone verify when user registry in system';

-- Data exporting was unselected.
-- Dumping structure for table ZCC_SSO.AMC_ROLE
DROP TABLE IF EXISTS `AMC_ROLE`;
CREATE TABLE IF NOT EXISTS `AMC_ROLE` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` char(20) NOT NULL DEFAULT '0' COMMENT '角色名称',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4;

-- Data exporting was unselected.
-- Dumping structure for table ZCC_SSO.AMC_ROLE_PERMISSION
DROP TABLE IF EXISTS `AMC_ROLE_PERMISSION`;
CREATE TABLE IF NOT EXISTS `AMC_ROLE_PERMISSION` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `role_id` bigint(20) DEFAULT NULL,
  `permission_id` bigint(20) DEFAULT NULL,
  `create_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `create_by` bigint(20) NOT NULL DEFAULT '-1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `role_permission` (`role_id`,`permission_id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COMMENT='角色权限';

-- Data exporting was unselected.
-- Dumping structure for table ZCC_SSO.AMC_USER
DROP TABLE IF EXISTS `AMC_USER`;
CREATE TABLE IF NOT EXISTS `AMC_USER` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` char(20) NOT NULL DEFAULT '-1' COMMENT '姓名',
  `password` varchar(255) NOT NULL DEFAULT '-1' COMMENT '密码',
  `mobile_phone` char(20) NOT NULL DEFAULT '-1' COMMENT '手机号码',
  `email` varchar(20) NOT NULL DEFAULT '-1' COMMENT '邮箱',
  `dept_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '部门id',
  `company_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '用户公司id',
  `valid` int(2) NOT NULL DEFAULT '-1' COMMENT '是否有效',
  `title` int(2) NOT NULL DEFAULT '-1' COMMENT '职位',
  `create_by` bigint(20) NOT NULL DEFAULT '-1' COMMENT '创建者id',
  `create_date` datetime NOT NULL DEFAULT '1900-01-01 00:00:00' COMMENT '创建日期',
  `update_by` bigint(20) NOT NULL DEFAULT '-1' COMMENT '更新者id',
  `update_date` datetime NOT NULL DEFAULT '1900-01-01 00:00:00' COMMENT '更新日期',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table ZCC_SSO.AMC_USER_ROLE
DROP TABLE IF EXISTS `AMC_USER_ROLE`;
CREATE TABLE IF NOT EXISTS `AMC_USER_ROLE` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `create_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
  `create_by` bigint(20) NOT NULL DEFAULT '-1' COMMENT '创建者id',
  `update_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新日期',
  `update_by` bigint(20) NOT NULL DEFAULT '-1' COMMENT '更新者id',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_role` (`user_id`,`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COMMENT='AMC 用户角色关系表\r\n';

-- Data exporting was unselected.
-- Dumping structure for table ZCC_SSO.AMC_WECHAT_USER
DROP TABLE IF EXISTS `AMC_WECHAT_USER`;
CREATE TABLE IF NOT EXISTS `AMC_WECHAT_USER` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `wechat_union_id` varchar(50) DEFAULT '0' COMMENT '微信unionid',
  `wechat_openid` varchar(50) DEFAULT '0' COMMENT '微信openid',
  `phone_number` char(50) DEFAULT NULL COMMENT '用户手机号',
  `session_key` varchar(50) DEFAULT NULL COMMENT '微信sessionkey',
  `user_name` varchar(50) DEFAULT NULL COMMENT '用户姓名',
  `gender` varchar(5) DEFAULT NULL COMMENT '性别',
  `location` varchar(20) DEFAULT NULL COMMENT '地理位置',
  `login_state` int(2) NOT NULL DEFAULT '-1' COMMENT '登陆状态',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Data exporting was unselected.
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
