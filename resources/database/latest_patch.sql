ALTER TABLE `billiongoods`.`store_article`
CHANGE COLUMN `wholesaler` `wholesaler` TINYINT(4) NULL DEFAULT NULL
AFTER `buyPrimordialPrice`,
CHANGE COLUMN `referenceId` `referenceUri` VARCHAR(255) NULL DEFAULT NULL;
