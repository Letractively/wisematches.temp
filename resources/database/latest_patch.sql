SET @OLD_UNIQUE_CHECKS = @@UNIQUE_CHECKS, UNIQUE_CHECKS = 0;
SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS = 0;
SET @OLD_SQL_MODE = @@SQL_MODE, SQL_MODE = 'TRADITIONAL,ALLOW_INVALID_DATES';

ALTER TABLE `billiongoods`.`account_lock`
DROP COLUMN `account`,
ADD COLUMN `account` BIGINT(20) NOT NULL
FIRST,
DROP PRIMARY KEY,
ADD PRIMARY KEY (`account`);

ALTER TABLE `billiongoods`.`store_product`
ADD INDEX `fk_store_product_store_category1_idx` (`categoryId` ASC),
DROP INDEX `registration_global_index`,
ADD INDEX `registration_global_index` (`registrationDate` DESC),
DROP INDEX `fk_store_product_store_category1_idx`;

ALTER TABLE `billiongoods`.`store_product_image`
ADD INDEX `fk_store_product_image_store_product1_idx` (`productId` ASC),
DROP INDEX `fk_store_product_image_store_product1_idx`;

ALTER TABLE `billiongoods`.`store_category_attribute`
ADD INDEX `fk_store_category_attribute_store_category1_idx` (`categoryId` ASC),
ADD INDEX `fk_store_category_attribute_store_attribute1_idx` (`attributeId` ASC),
DROP INDEX `fk_store_category_attribute_store_attribute1_idx`,
DROP INDEX `fk_store_category_attribute_store_category1_idx`;

ALTER TABLE `billiongoods`.`store_product_option`
ADD INDEX `fk_store_product_option_store_product1_idx` (`productId` ASC),
ADD INDEX `fk_store_product_option_store_attribute1_idx` (`attributeId` ASC),
DROP INDEX `fk_store_product_option_store_attribute1_idx`,
DROP INDEX `fk_store_product_option_store_product1_idx`;

ALTER TABLE `billiongoods`.`store_basket_option`
ADD INDEX `fk_store_basket_option_store_basket_item1_idx` (`basketItemId` ASC),
DROP INDEX `fk_store_basket_option_store_basket_item1_idx`;

ALTER TABLE `billiongoods`.`store_basket_item`
ADD INDEX `fk_store_basket_item_store_basket1_idx` (`basket` ASC),
ADD INDEX `fk_store_basket_item_store_product1_idx` (`product` ASC),
DROP INDEX `fk_store_basket_item_store_product1_idx`,
DROP INDEX `fk_store_basket_item_store_basket1_idx`;

ALTER TABLE `billiongoods`.`account_role`
CHANGE COLUMN `pid` `account` BIGINT(20) NOT NULL,
CHANGE COLUMN `role` `role` ENUM('admin', 'moderator') NOT NULL;

ALTER TABLE `billiongoods`.`store_order`
ADD INDEX `INDEX_state` (`state` ASC),
ADD INDEX `INDEX_token` (`token` ASC);

ALTER TABLE `billiongoods`.`store_order_item`
ADD INDEX `fk_store_order_item_store_product1_idx` (`product` ASC);

ALTER TABLE `billiongoods`.`paypal_transaction`
CHANGE COLUMN `orderId` `orderId` BIGINT(20) NOT NULL,
ADD INDEX `INDEX_token` (`token` ASC),
ADD INDEX `fk_paypal_transaction_store_order1_idx` (`orderId` ASC);

ALTER TABLE `billiongoods`.`account_personality`
ADD INDEX `email_INDEX` (`email` ASC),
DROP INDEX `id_UNIQUE`;

ALTER TABLE `billiongoods`.`store_group_item`
ADD INDEX `fk_store_group_item_store_product1_idx` (`productId` ASC);

ALTER TABLE `billiongoods`.`store_product_relationship`
ADD INDEX `fk_store_product_relationship_store_group1_idx` (`groupId` ASC);

ALTER TABLE `billiongoods`.`service_price_renewal`
ADD INDEX `fk_service_price_renewal_store_product1_idx` (`productId` ASC);

ALTER TABLE `billiongoods`.`store_showcase`
ADD INDEX `fk_store_showcase_store_category1_idx` (`category` ASC);

ALTER TABLE `billiongoods`.`account_lock`
ADD CONSTRAINT `fk_account_lock_account_personality1`
FOREIGN KEY (`account`)
REFERENCES `billiongoods`.`account_personality` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `billiongoods`.`account_recovery`
ADD CONSTRAINT `fk_account_recovery_account_personality1`
FOREIGN KEY (`account`)
REFERENCES `billiongoods`.`account_personality` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `billiongoods`.`store_product`
ADD CONSTRAINT `fk_store_product_store_category1`
FOREIGN KEY (`categoryId`)
REFERENCES `billiongoods`.`store_category` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `billiongoods`.`store_product_image`
ADD CONSTRAINT `fk_store_product_image_store_product1`
FOREIGN KEY (`productId`)
REFERENCES `billiongoods`.`store_product` (`id`)
  ON DELETE CASCADE
  ON UPDATE NO ACTION;

ALTER TABLE `billiongoods`.`store_category_attribute`
ADD CONSTRAINT `fk_store_category_attribute_store_category1`
FOREIGN KEY (`categoryId`)
REFERENCES `billiongoods`.`store_category` (`id`)
  ON DELETE CASCADE
  ON UPDATE NO ACTION,
ADD CONSTRAINT `fk_store_category_attribute_store_attribute1`
FOREIGN KEY (`attributeId`)
REFERENCES `billiongoods`.`store_attribute` (`id`)
  ON DELETE CASCADE
  ON UPDATE NO ACTION;

ALTER TABLE `billiongoods`.`store_product_option`
ADD CONSTRAINT `fk_store_product_option_store_product1`
FOREIGN KEY (`productId`)
REFERENCES `billiongoods`.`store_product` (`id`)
  ON DELETE CASCADE
  ON UPDATE NO ACTION,
ADD CONSTRAINT `fk_store_product_option_store_attribute1`
FOREIGN KEY (`attributeId`)
REFERENCES `billiongoods`.`store_attribute` (`id`)
  ON DELETE CASCADE
  ON UPDATE NO ACTION;

ALTER TABLE `billiongoods`.`store_product_property`
ADD CONSTRAINT `fk_store_product_property_store_product1`
FOREIGN KEY (`productId`)
REFERENCES `billiongoods`.`store_product` (`id`)
  ON DELETE CASCADE
  ON UPDATE NO ACTION,
ADD CONSTRAINT `fk_store_product_property_store_attribute1`
FOREIGN KEY (`attributeId`)
REFERENCES `billiongoods`.`store_attribute` (`id`)
  ON DELETE CASCADE
  ON UPDATE NO ACTION;

ALTER TABLE `billiongoods`.`store_basket_option`
ADD CONSTRAINT `fk_store_basket_option_store_basket1`
FOREIGN KEY (`basketId`)
REFERENCES `billiongoods`.`store_basket` (`pid`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION,
ADD CONSTRAINT `fk_store_basket_option_store_basket_item1`
FOREIGN KEY (`basketItemId`)
REFERENCES `billiongoods`.`store_basket_item` (`number`)
  ON DELETE CASCADE
  ON UPDATE NO ACTION,
ADD CONSTRAINT `fk_store_basket_option_store_attribute1`
FOREIGN KEY (`basketId`)
REFERENCES `billiongoods`.`store_attribute` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `billiongoods`.`store_basket_item`
ADD CONSTRAINT `fk_store_basket_item_store_basket1`
FOREIGN KEY (`basket`)
REFERENCES `billiongoods`.`store_basket` (`pid`)
  ON DELETE CASCADE
  ON UPDATE NO ACTION,
ADD CONSTRAINT `fk_store_basket_item_store_product1`
FOREIGN KEY (`product`)
REFERENCES `billiongoods`.`store_product` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `billiongoods`.`account_role`
ADD CONSTRAINT `fk_account_role_account_personality`
FOREIGN KEY (`account`)
REFERENCES `billiongoods`.`account_personality` (`id`)
  ON DELETE CASCADE
  ON UPDATE NO ACTION;

ALTER TABLE `billiongoods`.`store_order_item`
ADD CONSTRAINT `fk_store_order_item_store_order1`
FOREIGN KEY (`orderId`)
REFERENCES `billiongoods`.`store_order` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION,
ADD CONSTRAINT `fk_store_order_item_store_product1`
FOREIGN KEY (`product`)
REFERENCES `billiongoods`.`store_product` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `billiongoods`.`paypal_transaction`
ADD CONSTRAINT `fk_paypal_transaction_store_order1`
FOREIGN KEY (`orderId`)
REFERENCES `billiongoods`.`store_order` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `billiongoods`.`store_group_item`
ADD CONSTRAINT `fk_store_group_item_store_group1`
FOREIGN KEY (`groupId`)
REFERENCES `billiongoods`.`store_group` (`id`)
  ON DELETE CASCADE
  ON UPDATE NO ACTION,
ADD CONSTRAINT `fk_store_group_item_store_product1`
FOREIGN KEY (`productId`)
REFERENCES `billiongoods`.`store_product` (`id`)
  ON DELETE CASCADE
  ON UPDATE NO ACTION;

ALTER TABLE `billiongoods`.`store_product_relationship`
ADD CONSTRAINT `fk_store_product_relationship_store_group1`
FOREIGN KEY (`groupId`)
REFERENCES `billiongoods`.`store_group` (`id`)
  ON DELETE CASCADE
  ON UPDATE NO ACTION,
ADD CONSTRAINT `fk_store_product_relationship_store_product1`
FOREIGN KEY (`productId`)
REFERENCES `billiongoods`.`store_product` (`id`)
  ON DELETE CASCADE
  ON UPDATE NO ACTION;

ALTER TABLE `billiongoods`.`service_price_renewal`
ADD CONSTRAINT `fk_service_price_renewal_store_product1`
FOREIGN KEY (`productId`)
REFERENCES `billiongoods`.`store_product` (`id`)
  ON DELETE CASCADE
  ON UPDATE NO ACTION;

ALTER TABLE `billiongoods`.`store_showcase`
ADD CONSTRAINT `fk_store_showcase_store_category1`
FOREIGN KEY (`category`)
REFERENCES `billiongoods`.`store_category` (`id`)
  ON DELETE CASCADE
  ON UPDATE NO ACTION;

ALTER TABLE `billiongoods`.`store_order_log`
ADD CONSTRAINT `fk_store_order_log_store_order1`
FOREIGN KEY (`orderId`)
REFERENCES `billiongoods`.`store_order` (`id`)
  ON DELETE CASCADE
  ON UPDATE NO ACTION;


SET SQL_MODE = @OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS = @OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS = @OLD_UNIQUE_CHECKS;
