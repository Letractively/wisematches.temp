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
