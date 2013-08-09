SET @OLD_UNIQUE_CHECKS = @@UNIQUE_CHECKS, UNIQUE_CHECKS = 0;
SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS = 0;
SET @OLD_SQL_MODE = @@SQL_MODE, SQL_MODE = 'TRADITIONAL';

CREATE TABLE IF NOT EXISTS `billiongoods`.`account_role` (
  `pid`  BIGINT(20)  NOT NULL,
  `role` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`pid`))
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8
  COLLATE = utf8_general_ci;

ALTER TABLE `billiongoods`.`account_personality` CHANGE COLUMN `nickname` `username` VARCHAR(100)
CHARACTER SET 'utf8'
COLLATE 'utf8_general_ci' NOT NULL
, DROP INDEX `username_UNIQUE`;

ALTER TABLE `billiongoods`.`account_personality` DROP COLUMN `username`, ADD COLUMN `username` VARCHAR(100)
CHARACTER SET 'utf8'
COLLATE 'utf8_general_ci' NOT NULL
AFTER `id`;

ALTER TABLE `billiongoods`.`store_article` ADD COLUMN `weight` FLOAT(11) NULL DEFAULT NULL
AFTER `categoryId`, CHANGE COLUMN `active` `active` TINYINT(4) NULL DEFAULT NULL
AFTER `previewImageId`;

ALTER TABLE `billiongoods`.`account_personality` DROP COLUMN `username`, ADD COLUMN `username` VARCHAR(100)
CHARACTER SET 'utf8'
COLLATE 'utf8_general_ci' NOT NULL
AFTER `id`;

CREATE TABLE IF NOT EXISTS `billiongoods`.`store_order` (
  `id`            BIGINT(20)   NOT NULL,
  `buyer`         BIGINT(20)   NULL DEFAULT NULL,
  `code`          VARCHAR(45)  NULL DEFAULT NULL,
  `addressName`   VARCHAR(120) NULL DEFAULT NULL,
  `addressRegion` VARCHAR(120) NULL DEFAULT NULL,
  `addressCity`   VARCHAR(120) NULL DEFAULT NULL,
  `addressPostal` VARCHAR(10)  NULL DEFAULT NULL,
  `addressStreet` VARCHAR(250) NULL DEFAULT NULL,
  `state`         INT(11)      NULL DEFAULT NULL,
  PRIMARY KEY (`id`))
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8
  COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `billiongoods`.`store_order_item` (
  `order`    BIGINT(20)   NOT NULL,
  `number`   INT(11)      NOT NULL,
  `code`     VARCHAR(12)  NULL DEFAULT NULL,
  `name`     VARCHAR(255) NULL DEFAULT NULL,
  `quantity` INT(11)      NULL DEFAULT NULL,
  `amount`   FLOAT(11)    NULL DEFAULT NULL,
  `weight`   FLOAT(11)    NULL DEFAULT NULL,
  `options`  VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`order`, `number`))
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8
  COLLATE = utf8_general_ci;

ALTER TABLE `billiongoods`.`store_order` CHANGE COLUMN `id` `id` BIGINT(20) NOT NULL AUTO_INCREMENT;

SET SQL_MODE = @OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS = @OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS = @OLD_UNIQUE_CHECKS;
