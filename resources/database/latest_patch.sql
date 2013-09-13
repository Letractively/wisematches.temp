INSERT INTO `account_personality` (`id`, `username`, `password`, `email`, `lastActivityDate`)
  VALUES (2, 'Admin', 'acba08066261328c4701fec357e8de2b911a35cac971f8d09b4f0978fffbd3369bc7408f26d53ad4', 'asd@asd.ru',
          NULL);

DELETE i FROM store_product_image i LEFT JOIN store_product p
    ON p.id = i.productId
WHERE p.id IS null;

DELETE i FROM store_product_property i LEFT JOIN store_product p
    ON p.id = i.productId
WHERE p.id IS null;

DELETE i FROM paypal_transaction i LEFT JOIN store_order p
    ON p.id = i.orderId
WHERE p.id IS null;

DELETE i FROM store_group_item i LEFT JOIN store_group p
    ON p.id = i.groupId
WHERE p.id IS null;

DELETE i FROM store_group_item i LEFT JOIN store_product p
    ON p.id = i.productId
WHERE p.id IS null;

ALTER TABLE `billiongoods`.`account_lock`
CHANGE COLUMN `playerId` `account` BIGINT(20) NOT NULL,
DROP INDEX `userId_UNIQUE`;

ALTER TABLE `billiongoods`.`store_product`
ADD INDEX `state_global_index` (`state` ASC),
ADD INDEX `fk_store_product_store_category1_idx` (`categoryId` ASC),
ADD INDEX `price_global_index` (`price` ASC),
ADD INDEX `registration_global_index` (`registrationDate` DESC),
DROP INDEX `REG_DATE`,
DROP INDEX `PRICE`,
DROP INDEX `ACTIVE`;

ALTER TABLE `billiongoods`.`store_product_image`
ADD INDEX `fk_store_product_image_store_product1_idx` (`productId` ASC),
ADD INDEX `position_order_index` (`position` ASC)
  COMMENT 'Used in order by';

ALTER TABLE `billiongoods`.`store_category_attribute`
ADD INDEX `fk_store_category_attribute_store_category1_idx` (`categoryId` ASC),
ADD INDEX `fk_store_category_attribute_store_attribute1_idx` (`attributeId` ASC);

ALTER TABLE `billiongoods`.`store_product_option`
ADD INDEX `fk_store_product_option_store_product1_idx` (`productId` ASC),
ADD INDEX `fk_store_product_option_store_attribute1_idx` (`attributeId` ASC),
ADD INDEX `position_order_index` (`position` ASC)
  COMMENT 'Used in order by';

ALTER TABLE `billiongoods`.`store_product_property`
ADD INDEX `position_order_index` (`position` ASC)
  COMMENT 'used in order by';

ALTER TABLE `billiongoods`.`store_basket_option`
ADD INDEX `fk_store_basket_option_store_basket_item1_idx` (`basketItemId` ASC);

ALTER TABLE `billiongoods`.`store_basket_item`
ADD INDEX `fk_store_basket_item_store_basket1_idx` (`basket` ASC),
ADD INDEX `fk_store_basket_item_store_product1_idx` (`product` ASC);

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
DROP INDEX `id_UNIQUE`,
COMMENT = 'The base table that contains information about a player';

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
  ON DELETE CASCADE
  ON UPDATE NO ACTION;

ALTER TABLE `billiongoods`.`account_recovery`
ADD CONSTRAINT `fk_account_recovery_account_personality1`
FOREIGN KEY (`account`)
REFERENCES `billiongoods`.`account_personality` (`id`)
  ON DELETE CASCADE
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
FOREIGN KEY (`attributeId`)
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

# TYPES FIXING
ALTER TABLE `billiongoods`.`account_lock`
DROP FOREIGN KEY `fk_account_lock_account_personality1`;

ALTER TABLE `billiongoods`.`store_basket_item`
DROP FOREIGN KEY `fk_store_basket_item_store_product1`;

ALTER TABLE `billiongoods`.`account_role`
DROP FOREIGN KEY `fk_account_role_account_personality`;

ALTER TABLE `billiongoods`.`store_order_item`
DROP FOREIGN KEY `fk_store_order_item_store_product1`;

ALTER TABLE `billiongoods`.`service_price_renewal`
DROP FOREIGN KEY `fk_service_price_renewal_store_product1`;

ALTER TABLE `billiongoods`.`store_order_log`
DROP FOREIGN KEY `fk_store_order_log_store_order1`;

ALTER TABLE `billiongoods`.`account_lock`
DROP COLUMN `account`,
ADD COLUMN `account` BIGINT(20) NOT NULL
FIRST,
DROP PRIMARY KEY,
ADD PRIMARY KEY (`account`);

ALTER TABLE `billiongoods`.`store_category`
CHANGE COLUMN `name` `name` VARCHAR(100) NOT NULL;

ALTER TABLE `billiongoods`.`store_product`
CHANGE COLUMN `stockSold` `stockSold` INT(11) NOT NULL DEFAULT 0,
CHANGE COLUMN `state` `state` SMALLINT(6) NOT NULL,
ADD INDEX `fk_store_product_store_category1_idx` (`categoryId` ASC),
DROP INDEX `registration_global_index`,
ADD INDEX `registration_global_index` (`registrationDate` DESC),
ADD INDEX `stock_sold_index` (`stockSold` DESC),
DROP INDEX `fk_store_product_store_category1_idx`;

ALTER TABLE `billiongoods`.`store_product_image`
ADD INDEX `fk_store_product_image_store_product1_idx` (`productId` ASC),
DROP INDEX `fk_store_product_image_store_product1_idx`;

ALTER TABLE `billiongoods`.`store_attribute`
CHANGE COLUMN `id` `id` INT(11) NOT NULL;

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
DROP INDEX `fk_store_basket_option_store_attribute1`,
ADD INDEX `fk_store_basket_option_store_attribute1_idx` (`attributeId` ASC),
DROP INDEX `fk_store_basket_option_store_basket_item1_idx`;

ALTER TABLE `billiongoods`.`store_basket`
CHANGE COLUMN `creationTime` `creationTime` DATETIME NOT NULL,
CHANGE COLUMN `updatingTime` `updatingTime` DATETIME NOT NULL;

ALTER TABLE `billiongoods`.`store_basket_item`
CHANGE COLUMN `product` `product` INT(11) NOT NULL
AFTER `basket`,
CHANGE COLUMN `quantity` `quantity` INT(11) NOT NULL,
ADD INDEX `fk_store_basket_item_store_basket1_idx` (`basket` ASC),
ADD INDEX `fk_store_basket_item_store_product1_idx` (`product` ASC),
DROP INDEX `fk_store_basket_item_store_product1_idx`,
DROP INDEX `fk_store_basket_item_store_basket1_idx`;

ALTER TABLE `billiongoods`.`account_role`
DROP COLUMN `account`,
ADD COLUMN `account` BIGINT(20) NOT NULL
FIRST,
DROP PRIMARY KEY,
ADD PRIMARY KEY (`account`, `role`);

ALTER TABLE `billiongoods`.`store_order`
CHANGE COLUMN `amount` `amount` DECIMAL(10, 4) NOT NULL,
CHANGE COLUMN `shipment` `shipment` DECIMAL(10, 4) NOT NULL,
CHANGE COLUMN `exchangeRate` `exchangeRate` DECIMAL(10, 4) NOT NULL,
CHANGE COLUMN `shipmentType` `shipmentType` TINYINT(4) NOT NULL,
CHANGE COLUMN `creationTime` `creationTime` DATETIME NOT NULL,
CHANGE COLUMN `tracking` `tracking` INT(1) NOT NULL,
CHANGE COLUMN `state` `state` INT(11) NOT NULL,
CHANGE COLUMN `timestamp` `timestamp` DATETIME NOT NULL;

ALTER TABLE `billiongoods`.`store_order_item`
CHANGE COLUMN `product` `product` INT(11) NOT NULL,
CHANGE COLUMN `name` `name` VARCHAR(255) NOT NULL,
CHANGE COLUMN `quantity` `quantity` INT(11) NOT NULL,
CHANGE COLUMN `amount` `amount` DECIMAL(10, 4) NOT NULL,
CHANGE COLUMN `weight` `weight` DECIMAL(10, 4) NOT NULL,
ADD INDEX `fk_store_order_item_store_product1_idx` (`product` ASC),
DROP INDEX `fk_store_order_item_store_product1_idx`;

ALTER TABLE `billiongoods`.`paypal_transaction`
CHANGE COLUMN `amount` `amount` DECIMAL(10, 4) NOT NULL,
CHANGE COLUMN `shipment` `shipment` DECIMAL(10, 4) NOT NULL,
CHANGE COLUMN `feeAmount` `feeAmount` DECIMAL(10, 4) NULL DEFAULT NULL,
CHANGE COLUMN `grossAmount` `grossAmount` DECIMAL(10, 4) NULL DEFAULT NULL,
CHANGE COLUMN `settleAmount` `settleAmount` DECIMAL(10, 4) NULL DEFAULT NULL,
CHANGE COLUMN `taxAmount` `taxAmount` DECIMAL(10, 4) NULL DEFAULT NULL,
ADD INDEX `fk_paypal_transaction_store_order1_idx` (`orderId` ASC),
DROP INDEX `fk_paypal_transaction_store_order1_idx`;

ALTER TABLE `billiongoods`.`service_exchange`
CHANGE COLUMN `timestamp` `timestamp` DATETIME NOT NULL,
CHANGE COLUMN `exchangeRate` `exchangeRate` DECIMAL(10, 4) NOT NULL;

ALTER TABLE `billiongoods`.`store_group`
CHANGE COLUMN `name` `name` VARCHAR(145) NOT NULL;

ALTER TABLE `billiongoods`.`store_group_item`
ADD INDEX `fk_store_group_item_store_product1_idx` (`productId` ASC),
DROP INDEX `fk_store_group_item_store_product1_idx`;

ALTER TABLE `billiongoods`.`store_product_relationship`
ADD INDEX `fk_store_product_relationship_store_group1_idx` (`groupId` ASC),
DROP INDEX `fk_store_product_relationship_store_group1_idx`;

ALTER TABLE `billiongoods`.`service_price_renewal`
CHANGE COLUMN `productId` `productId` INT(11) NOT NULL,
CHANGE COLUMN `timestamp` `timestamp` DATETIME NOT NULL,
CHANGE COLUMN `oldPrice` `oldPrice` DECIMAL(10, 4) NOT NULL,
CHANGE COLUMN `oldPrimordialPrice` `oldPrimordialPrice` DECIMAL(10, 4) NULL DEFAULT NULL,
CHANGE COLUMN `oldSupplierPrice` `oldSupplierPrice` DECIMAL(10, 4) NOT NULL,
CHANGE COLUMN `oldSupplierPrimordialPrice` `oldSupplierPrimordialPrice` DECIMAL(10, 4) NULL DEFAULT NULL,
CHANGE COLUMN `newPrice` `newPrice` DECIMAL(10, 4) NOT NULL,
CHANGE COLUMN `newPrimordialPrice` `newPrimordialPrice` DECIMAL(10, 4) NULL DEFAULT NULL,
CHANGE COLUMN `newSupplierPrice` `newSupplierPrice` DECIMAL(10, 4) NOT NULL,
CHANGE COLUMN `newSupplierPrimordialPrice` `newSupplierPrimordialPrice` DECIMAL(10, 4) NULL DEFAULT NULL,
ADD INDEX `fk_service_price_renewal_store_product1_idx` (`productId` ASC),
DROP INDEX `fk_service_price_renewal_store_product1_idx`;

ALTER TABLE `billiongoods`.`store_showcase`
ADD INDEX `fk_store_showcase_store_category1_idx` (`category` ASC),
DROP INDEX `fk_store_showcase_store_category1_idx`;

ALTER TABLE `billiongoods`.`store_order_log`
CHANGE COLUMN `orderState` `orderState` TINYINT(4) NOT NULL
AFTER `timestamp`,
CHANGE COLUMN `orderId` `orderId` BIGINT(20) NOT NULL,
CHANGE COLUMN `code` `code` VARCHAR(45) NOT NULL,
CHANGE COLUMN `timestamp` `timestamp` DATETIME NOT NULL,
DROP INDEX `fk_store_order_log_store_order1`;

ALTER TABLE `billiongoods`.`account_lock`
ADD CONSTRAINT `fk_account_lock_account_personality1`
FOREIGN KEY (`account`)
REFERENCES `billiongoods`.`account_personality` (`id`)
  ON DELETE CASCADE
  ON UPDATE NO ACTION;

ALTER TABLE `billiongoods`.`store_basket_item`
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
ADD CONSTRAINT `fk_store_order_item_store_product1`
FOREIGN KEY (`product`)
REFERENCES `billiongoods`.`store_product` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `billiongoods`.`service_price_renewal`
ADD CONSTRAINT `fk_service_price_renewal_store_product1`
FOREIGN KEY (`productId`)
REFERENCES `billiongoods`.`store_product` (`id`)
  ON DELETE CASCADE
  ON UPDATE NO ACTION;

ALTER TABLE `billiongoods`.`store_order_log`
ADD CONSTRAINT `fk_store_order_log_store_order1`
FOREIGN KEY (`orderId`)
REFERENCES `billiongoods`.`store_order` (`id`)
  ON DELETE CASCADE
  ON UPDATE NO ACTION;

ALTER TABLE `billiongoods`.`service_exchange`
ADD INDEX `timestamp_index` (`timestamp` DESC);

ALTER TABLE `billiongoods`.`store_product_property`
ADD INDEX `property_value_index` (`value` ASC);