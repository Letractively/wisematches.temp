SET @OLD_UNIQUE_CHECKS = @@UNIQUE_CHECKS, UNIQUE_CHECKS = 0;
SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS = 0;
SET @OLD_SQL_MODE = @@SQL_MODE, SQL_MODE = 'TRADITIONAL';

ALTER TABLE `billiongoods`.`account_personality` DROP COLUMN `timezone`, DROP COLUMN `language`, ADD COLUMN `lastActivityDate` DATETIME NULL DEFAULT NULL
AFTER `email`;

ALTER TABLE `billiongoods`.`store_article_image` DROP COLUMN `position`, ADD COLUMN `position` TINYINT(4) NOT NULL
AFTER `imageId`;

ALTER TABLE `billiongoods`.`store_article_accessory` DROP COLUMN `position`, ADD COLUMN `position` TINYINT(4) NOT NULL
AFTER `accessoryId`;

ALTER TABLE `billiongoods`.`store_article_option` DROP COLUMN `position`, ADD COLUMN `position` TINYINT(4) NOT NULL
AFTER `value`;

ALTER TABLE `billiongoods`.`store_article_property` DROP COLUMN `position`, ADD COLUMN `position` TINYINT(4) NOT NULL
AFTER `value`;

DROP TABLE IF EXISTS `billiongoods`.`player_activity`;

DROP TABLE IF EXISTS `billiongoods`.`account_membership`;


SET SQL_MODE = @OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS = @OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS = @OLD_UNIQUE_CHECKS;


SET @OLD_UNIQUE_CHECKS = @@UNIQUE_CHECKS, UNIQUE_CHECKS = 0;
SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS = 0;
SET @OLD_SQL_MODE = @@SQL_MODE, SQL_MODE = 'TRADITIONAL';

CREATE TABLE IF NOT EXISTS `billiongoods`.`store_basket_option` (
  `articleId`   INT(11)     NOT NULL,
  `attributeId` INT(11)     NOT NULL,
  `value`       VARCHAR(45) NULL DEFAULT NULL,
  `position`    TINYINT(4)  NOT NULL,
  PRIMARY KEY (`articleId`, `attributeId`))
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8
  COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `billiongoods`.`store_basket` (
  `pid`           BIGINT(20) NOT NULL,
  `creationTime`  DATETIME   NULL DEFAULT NULL,
  `updatingTime`  DATETIME   NULL DEFAULT NULL,
  `lastItemIndex` INT(11)    NULL DEFAULT NULL,
  PRIMARY KEY (`pid`))
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8
  COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `billiongoods`.`store_basket_item` (
  `basket`   BIGINT(20) NOT NULL,
  `index`    INT(11)    NOT NULL,
  `quantity` INT(11)    NULL DEFAULT NULL,
  `article`  INT(11)    NULL DEFAULT NULL,
  PRIMARY KEY (`basket`, `index`))
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8
  COLLATE = utf8_general_ci;

ALTER TABLE `billiongoods`.`store_basket` ADD COLUMN `expirationDays` INT(11) NULL DEFAULT NULL
AFTER `lastItemIndex`;

ALTER TABLE `billiongoods`.`store_basket` DROP COLUMN `lastItemIndex`;

ALTER TABLE `billiongoods`.`store_basket_item` CHANGE COLUMN `index` `number` INT(11) NOT NULL
FIRST
, DROP PRIMARY KEY
, ADD PRIMARY KEY (`number`, `basket`);

ALTER TABLE `billiongoods`.`store_basket_option` CHANGE COLUMN `articleId` `basketItemId` INT(11) NOT NULL;

ALTER TABLE `billiongoods`.`store_basket_option` ADD COLUMN `basketId` BIGINT(20) NOT NULL
FIRST
, DROP PRIMARY KEY
, ADD PRIMARY KEY (`basketId`, `basketItemId`, `attributeId`);

SET SQL_MODE = @OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS = @OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS = @OLD_UNIQUE_CHECKS;
