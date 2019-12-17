/*
SQLyog Ultimate v11.25 (64 bit)
MySQL - 8.0.17 : Database - finance
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`finance` /*!40100 DEFAULT CHARACTER SET utf8 COLLATE utf8_bin */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `finance`;

/*Table structure for table `stock_daily_deal` */

DROP TABLE IF EXISTS `stock_daily_deal`;

CREATE TABLE `stock_daily_deal` (
  `oid` int(11) NOT NULL AUTO_INCREMENT,
  `stock_id` int(11) NOT NULL COMMENT 'stock表的id',
  `current_stock_name` varchar(1024) COLLATE utf8_bin DEFAULT NULL COMMENT '股票的当前名字，因为中途股票会改名',
  `date_of_data` date NOT NULL COMMENT '该条数据属于的日期',
  `year` int(11) NOT NULL COMMENT '该条数据是哪一年',
  `month` int(11) NOT NULL COMMENT '该条数据是哪一月',
  `day` int(11) NOT NULL COMMENT '该条数据是哪一日',
  `day_of_week` int(11) NOT NULL COMMENT '该条数据是星期几，1即为星期一',
  `l_close` float(8,2) NOT NULL COMMENT '前一日收盘价',
  `t_open` float(8,2) NOT NULL COMMENT '今日开盘价',
  `t_close` float(8,2) NOT NULL COMMENT '今日收盘价',
  `max_price` float(8,2) NOT NULL COMMENT '今日最高价',
  `min_price` float(8,2) NOT NULL COMMENT '今日最低价',
  `min_max_price_amplitude` float(8,2) NOT NULL COMMENT '今日最大振幅：max-min',
  `lt_price_change` float(8,2) NOT NULL COMMENT '今日收盘价较上一日收盘价变化:t_close-l_close',
  `lt_price_change_ratio` float(6,2) NOT NULL COMMENT '今日收盘价较上一日收盘价变化百分比:price_change/t_close*100',
  `open_close_price_change` float(8,2) NOT NULL COMMENT '今日收盘价较开盘价的变化:t_close-t_open',
  `open_close_price_change_ratio` float(7,2) NOT NULL COMMENT '今日收盘价较开盘价的变化百分比:price_open_close_change/t_open*100',
  `total_hand` bigint(20) unsigned DEFAULT NULL COMMENT '今日成交总手',
  `hand_change_ratio` float(7,2) DEFAULT NULL COMMENT '今日换手率',
  `turnover` double(20,2) DEFAULT NULL COMMENT '今日成交额',
  `total_market_cap` double(20,2) DEFAULT NULL COMMENT '总市值',
  `negotiable_market_cap` double(20,2) DEFAULT NULL COMMENT '流通市值',
  `entry_datetime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`oid`),
  UNIQUE KEY `stock_daily_dealsI1` (`stock_id`,`date_of_data`)
) ENGINE=InnoDB AUTO_INCREMENT=10086650 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*Table structure for table `stock_daily_deal_sync_log` */

DROP TABLE IF EXISTS `stock_daily_deal_sync_log`;

CREATE TABLE `stock_daily_deal_sync_log` (
  `oid` int(11) NOT NULL AUTO_INCREMENT,
  `stock_code` varchar(10) COLLATE utf8_bin NOT NULL,
  `current_latest_date` date DEFAULT NULL,
  `sync_start_date` date DEFAULT NULL,
  `sync_end_date` date DEFAULT NULL,
  `synce_status` varchar(10) COLLATE utf8_bin DEFAULT NULL,
  `sync_desc` varchar(1024) COLLATE utf8_bin DEFAULT NULL,
  `entry_datetime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`oid`)
) ENGINE=InnoDB AUTO_INCREMENT=17837 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*Table structure for table `stock_header` */

DROP TABLE IF EXISTS `stock_header`;

CREATE TABLE `stock_header` (
  `oid` int(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(10) COLLATE utf8_bin NOT NULL,
  `name` varchar(1024) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`oid`),
  UNIQUE KEY `stock_headerI1` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=3795 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/* Procedure structure for procedure `get_history_data` */

/*!50003 DROP PROCEDURE IF EXISTS  `get_history_data` */;

DELIMITER $$

/*!50003 CREATE DEFINER=`root`@`localhost` PROCEDURE `get_history_data`(in stock_code varchar(10), in find_change FLOAT(8,2), IN day_count INT, in model VARCHAR(10), in from_date date)
BEGIN
	
	DECLARE var_date date;
	DECLARE total_change FLOAT(10,2);
	DECLARE done INT DEFAULT 0;
	DECLARE find_date CURSOR FOR SELECT 
					  d.date_of_data
					FROM
					  `stock_header` h 
					  LEFT JOIN `stock_daily_deal` d 
					    ON h.oid = d.stock_id 
					WHERE d.lt_price_change_ratio < find_change
					  AND h.code = stock_code ;
	DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;
	
	DROP TABLE IF EXISTS result;
	CREATE TEMPORARY TABLE result(
		`oid` INT(11) NOT NULL AUTO_INCREMENT,
		`group_date` DATE NOT NULL,
		`cunrent_date` date,
		`price_change` FLOAT(10,2) NOT NULL,
		PRIMARY KEY (`oid`)
	);
	
	OPEN find_date;
	loop_h:LOOP
		FETCH find_date INTO var_date;
		IF done = 1 THEN
			LEAVE loop_h;
		END IF;
		
		/* INSERT INTO result(`group_date`, `cunrent_date`, `price_change`) SELECT 
										  var_date,
										  d.`date_of_data`,
										  d.lt_price_change_ratio
										FROM
										  `stock_header` h 
										  LEFT JOIN `stock_daily_deal` d 
										    ON h.oid = d.stock_id 
										WHERE h.code = stock_code 
										AND d.date_of_data > var_date
										ORDER BY d.`date_of_data` ASC LIMIT day_count; */
		
		SELECT SUM(a.lt_price_change_ratio) into total_change FROM (SELECT
							  d.lt_price_change_ratio
							FROM
							  `stock_header` h 
							  LEFT JOIN `stock_daily_deal` d 
							    ON h.oid = d.stock_id 
							WHERE h.code = stock_code 
							AND d.date_of_data > var_date
							ORDER BY d.`date_of_data` ASC LIMIT day_count) a;
		
		INSERT INTO result(`group_date`, `price_change`) values(var_date, total_change);
	
	END LOOP loop_h;
	CLOSE find_date;
	
	if from_date is null then
		
			IF model = 'up' THEN
				SELECT COUNT(1) FROM result WHERE price_change > 0;
			ELSEIF model = 'down' THEN
				SELECT COUNT(1) FROM result WHERE price_change < 0;
			ELSEIF model = 'up-sum' THEN
				SELECT SUM(price_change) FROM result WHERE price_change > 0;
			ELSEIF model = 'down-sum' THEN
				SELECT SUM(price_change) FROM result WHERE price_change < 0;
			ELSE 
				SELECT * FROM result;
			END IF;
		
	else
		
			IF model = 'up' THEN
				SELECT count(1) FROM result WHERE price_change > 0 and group_date> from_date;
			ELSEIF model = 'down' THEN
				SELECT COUNT(1) FROM result WHERE price_change < 0 AND group_date> from_date;
			ELSEIF model = 'up-sum' THEN
				SELECT SUM(price_change) FROM result WHERE price_change > 0 AND group_date> from_date;
			ELSEIF model = 'down-sum' THEN
				SELECT SUM(price_change) FROM result WHERE price_change < 0 AND group_date> from_date;
			ELSE 
				SELECT * FROM result where group_date> from_date;
			END IF;
		
	end if;
	
	
	DROP TABLE IF EXISTS result;
END */$$
DELIMITER ;

/* Procedure structure for procedure `get_max_new_hight_stock` */

/*!50003 DROP PROCEDURE IF EXISTS  `get_max_new_hight_stock` */;

DELIMITER $$

/*!50003 CREATE DEFINER=`root`@`localhost` PROCEDURE `get_max_new_hight_stock`(IN date_find DATE, IN day_count INT)
BEGIN
	
	DECLARE stock_code VARCHAR(10);
	DECLARE done INT DEFAULT 0;
	DECLARE sh CURSOR FOR SELECT `code` FROM `stock_header`;
	DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;
	
	DROP TABLE IF EXISTS result;
	CREATE TEMPORARY TABLE result(
		`oid` INT(11) NOT NULL AUTO_INCREMENT,
		`code` VARCHAR(10) NOT NULL,
		`name` VARCHAR(1024) DEFAULT NULL,
		`new_hight_date` DATE NOT NULL,
		`close_price` FLOAT(8,2) NOT NULL,
		PRIMARY KEY (`oid`)
	);
	
	OPEN sh;
	loop_h:LOOP
		FETCH sh INTO stock_code;
		IF done = 1 THEN
			LEAVE loop_h;
		END IF;
		
		INSERT INTO result(`code`, `name`, `new_hight_date`, `close_price`) SELECT 
										  SUBSTR(h1.`code`,3),
										  h1.`name`,
										  d1.`date_of_data`,
										  d1.`t_close`
										FROM
										  `stock_header` h1 
										  LEFT JOIN `stock_daily_deal` d1 
										    ON h1.`oid` = d1.`stock_id` 
										WHERE h1.`code`=stock_code AND
										d1.`date_of_data` = date_find
										AND d1.t_close  >  ALL (SELECT a.* FROM (SELECT 
										  d.max_price
										FROM
										  `stock_header` h 
										  LEFT JOIN `stock_daily_deal` d 
										    ON h.`oid` = d.`stock_id` 
										WHERE h.`code`=stock_code AND
										d.`date_of_data` < date_find
										ORDER BY d.`date_of_data` DESC LIMIT day_count) a);
	
	END LOOP loop_h;
	CLOSE sh; 
	SELECT `code` as 股票代码, `name` as 股票名称, `new_hight_date` as 新高日期, `close_price` as 新高收盘价 FROM result;
	DROP TABLE IF EXISTS result;
END */$$
DELIMITER ;

/* Procedure structure for procedure `get_new_hight_stock` */

/*!50003 DROP PROCEDURE IF EXISTS  `get_new_hight_stock` */;

DELIMITER $$

/*!50003 CREATE DEFINER=`root`@`localhost` PROCEDURE `get_new_hight_stock`(IN date_find date, in day_count int)
BEGIN
	
	DECLARE stock_code VARCHAR(10);
	DECLARE done INT DEFAULT 0;
	DECLARE sh CURSOR FOR select `code` from `stock_header`;
	DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;
	
	DROP TABLE IF EXISTS result;
	CREATE TEMPORARY TABLE result(
		`oid` int(11) NOT NULL AUTO_INCREMENT,
		`code` varchar(10) NOT NULL,
		`name` varchar(1024) DEFAULT NULL,
		`new_hight_date` date NOT NULL,
		`close_price` float(8,2) NOT NULL,
		PRIMARY KEY (`oid`)
	);
	
	OPEN sh;
	loop_h:loop
		FETCH sh into stock_code;
		IF done = 1 THEN
			LEAVE loop_h;
		END IF;
		
		INSERT INTO result(`code`, `name`, `new_hight_date`, `close_price`) SELECT 
										  SubSTR(h1.`code`,3),
										  h1.`name`,
										  d1.`date_of_data`,
										  d1.`t_close`
										FROM
										  `stock_header` h1 
										  LEFT JOIN `stock_daily_deal` d1 
										    ON h1.`oid` = d1.`stock_id` 
										WHERE h1.`code`=stock_code AND
										d1.`date_of_data` = date_find
										AND d1.t_close  >  ALL (SELECT a.* FROM (SELECT 
										  CASE 
											WHEN d.t_close > d.t_open THEN d.t_close
											ELSE d.t_open
										      END
										FROM
										  `stock_header` h 
										  LEFT JOIN `stock_daily_deal` d 
										    ON h.`oid` = d.`stock_id` 
										WHERE h.`code`=stock_code AND
										d.`date_of_data` < date_find
										ORDER BY d.`date_of_data` DESC LIMIT day_count) a);
	
	END LOOP loop_h;
	CLOSE sh; 
	select `code`, `name`, `new_hight_date`, `close_price` from result;
	DROP TABLE IF EXISTS result;
END */$$
DELIMITER ;

/* Procedure structure for procedure `get_new_hight_stock_test` */

/*!50003 DROP PROCEDURE IF EXISTS  `get_new_hight_stock_test` */;

DELIMITER $$

/*!50003 CREATE DEFINER=`root`@`localhost` PROCEDURE `get_new_hight_stock_test`(IN date_find DATE, IN day_count INT)
BEGIN
	
	DECLARE stock_code VARCHAR(10);
	DECLARE done INT DEFAULT 0;
	DECLARE sh CURSOR FOR SELECT `code` FROM `stock_header`;
	DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;
	
	DROP TABLE IF EXISTS result;
	CREATE TEMPORARY TABLE result(
		`oid` INT(11) NOT NULL AUTO_INCREMENT,
		`code` VARCHAR(10) NOT NULL,
		`name` VARCHAR(1024) DEFAULT NULL,
		`new_hight_date` DATE NOT NULL,
		`close_price` FLOAT(8,2) NOT NULL,
		PRIMARY KEY (`oid`)
	);
	
	OPEN sh;
	loop_h:LOOP
		FETCH sh INTO stock_code;
		IF done = 1 THEN
			LEAVE loop_h;
		END IF;
		
		INSERT INTO result(`code`, `name`, `new_hight_date`, `close_price`) SELECT 
										  SUBSTR(h1.`code`,3),
										  h1.`name`,
										  d1.`date_of_data`,
										  d1.`t_close`
										FROM
										  `stock_header` h1 
										  LEFT JOIN `stock_daily_deal` d1 
										    ON h1.`oid` = d1.`stock_id` 
										    and h1.`code`=stock_code
										WHERE
										d1.`date_of_data` = date_find
										AND d1.t_close  >  ALL (SELECT a.* FROM (SELECT 
										  CASE 
											WHEN d.t_close > d.t_open THEN d.t_close
											ELSE d.t_open
										      END
										FROM
										  `stock_header` h 
										  LEFT JOIN `stock_daily_deal` d 
										    ON h.`oid` = d.`stock_id`
										    and h.`code`=stock_code 
										WHERE
										d.`date_of_data` < date_find
										ORDER BY d.`date_of_data` DESC LIMIT day_count) a);
	
	END LOOP loop_h;
	CLOSE sh; 
	SELECT `code`, `name`, `new_hight_date`, `close_price` FROM result;
	DROP TABLE IF EXISTS result;
END */$$
DELIMITER ;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
