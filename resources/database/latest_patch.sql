ALTER TABLE `billiongoods`.`store_article`
ADD COLUMN `stockAvailable` INT(11) NULL DEFAULT NULL
AFTER `stockSold`,
ADD COLUMN `comment` VARCHAR(100) NULL DEFAULT NULL
AFTER `previewImageId`,
CHANGE COLUMN `restockDate` `restockDate` DATE NULL DEFAULT NULL
AFTER `stockAvailable`,
CHANGE COLUMN `soldCount` `stockSold` INT(11) NULL DEFAULT 0,
CHANGE COLUMN `active` `state` SMALLINT(6) NULL DEFAULT NULL,
DROP INDEX `REG_DATE`,
ADD INDEX `REG_DATE` (`registrationDate` DESC);

UPDATE store_article
SET state=2
WHERE state = 1;