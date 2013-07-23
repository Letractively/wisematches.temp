ALTER TABLE `billiongoods`.`store_category` ADD COLUMN `active` INT(1) NULL DEFAULT 0
AFTER `position`;

UPDATE store_category
SET active=1;