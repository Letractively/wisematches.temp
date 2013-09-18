ALTER TABLE `billiongoods`.`store_attribute`
ADD COLUMN `description` VARCHAR(255) NULL DEFAULT NULL
AFTER `unit`,
ADD COLUMN `type` TINYINT(4) NULL DEFAULT NULL
AFTER `description`;

UPDATE store_attribute
SET type=0;