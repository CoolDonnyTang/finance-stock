DELIMITER $$

USE `finance`$$

DROP PROCEDURE IF EXISTS `get_max_new_hight_stock`$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `get_max_new_hight_stock`(IN date_find DATE, IN day_count INT)
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
	SELECT `code` AS 股票代码, `name` AS 股票名称, `new_hight_date` AS 新高日期, `close_price` AS 新高收盘价 FROM result;
	DROP TABLE IF EXISTS result;
END$$

DELIMITER ;