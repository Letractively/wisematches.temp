SET @OLD_UNIQUE_CHECKS = @@UNIQUE_CHECKS, UNIQUE_CHECKS = 0;
SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS = 0;
SET @OLD_SQL_MODE = @@SQL_MODE, SQL_MODE = 'TRADITIONAL,ALLOW_INVALID_DATES';

ALTER TABLE `billiongoods`.`store_article` RENAME TO `billiongoods`.`store_product`;

ALTER TABLE `billiongoods`.`store_article_image`
CHANGE COLUMN `articleId` `productId` INT(11) NOT NULL, RENAME TO `billiongoods`.`store_product_image`;

ALTER TABLE `billiongoods`.`store_article_option`
CHANGE COLUMN `articleId` `productId` INT(11) NOT NULL, RENAME TO `billiongoods`.`store_product_option`;

ALTER TABLE `billiongoods`.`store_article_property`
CHANGE COLUMN `articleId` `productId` INT(11) NOT NULL, RENAME TO `billiongoods`.`store_product_property`;

ALTER TABLE `billiongoods`.`store_basket_item`
CHANGE COLUMN `article` `product` INT(11) NULL DEFAULT NULL;

ALTER TABLE `billiongoods`.`store_order_item`
CHANGE COLUMN `article` `product` INT(11) NULL DEFAULT NULL;

ALTER TABLE `billiongoods`.`store_group_item`
CHANGE COLUMN `articleId` `productId` INT(11) NOT NULL;

ALTER TABLE `billiongoods`.`store_article_relationship`
CHANGE COLUMN `articleId` `productId` INT(11) NOT NULL, RENAME TO `billiongoods`.`store_product_relationship`;

ALTER TABLE `billiongoods`.`service_price_renewal`
CHANGE COLUMN `articleId` `productId` INT(11) NULL DEFAULT NULL;


SET SQL_MODE = @OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS = @OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS = @OLD_UNIQUE_CHECKS;
