ALTER TABLE `billiongoods`.`store_attribute`
ADD COLUMN `description` VARCHAR(255) NULL DEFAULT NULL
AFTER `unit`,
ADD COLUMN `type` TINYINT(4) NULL DEFAULT NULL
AFTER `description`;

UPDATE store_attribute
SET type=0;

UPDATE store_product
SET price = (buyPrice + buyPrice * 0.20) * 35 + 10;

UPDATE store_product
SET primordialPrice = (buyPrimordialPrice + buyPrimordialPrice * 0.20) * 35 + 10
WHERE buyPrimordialPrice IS NOT null;