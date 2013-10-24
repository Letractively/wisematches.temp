#10.23
CREATE TABLE `billiongoods`.`store_coupon` (
  `id`             INT         NOT NULL AUTO_INCREMENT,
  `code`           VARCHAR(10) NOT NULL,
  `created`        DATETIME    NOT NULL,
  `amount`         DOUBLE      NOT NULL,
  `couponType`     TINYINT     NOT NULL,
  `referenceId`    INT         NOT NULL,
  `referenceType`  TINYINT     NOT NULL,
  `started`        DATETIME    NULL DEFAULT NULL,
  `finished`       DATETIME    NULL DEFAULT NULL,
  `scheduledCount` SMALLINT    NULL DEFAULT 0,
  `remainingCount` SMALLINT    NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `code_UNIQUE` (`code` ASC),
  INDEX `code_INDEX` (`code` ASC));

ALTER TABLE `billiongoods`.`store_coupon`
ADD COLUMN `closure` INT(1) NULL DEFAULT 0
AFTER `remainingCount`;

#10.22
ALTER TABLE `billiongoods`.`store_order`
CHANGE COLUMN `creationTime` `created` DATETIME NOT NULL,
ADD COLUMN `closed` DATETIME NULL
AFTER `created`;

UPDATE store_order
SET state=state - 1
WHERE state > 1;

ALTER TABLE `billiongoods`.`paypal_transaction`
DROP FOREIGN KEY `fk_paypal_transaction_store_order1`;
ALTER TABLE `billiongoods`.`paypal_transaction`
DROP INDEX `fk_paypal_transaction_store_order1_idx`,
ADD INDEX `INDEX_orderId` (`orderId` ASC);

ALTER TABLE `billiongoods`.`store_product`
ADD COLUMN `recommended` INT(1) NOT NULL DEFAULT 0
AFTER `state`,
ADD INDEX `recommended_index` (`recommended` ASC);

# 10.18
ALTER TABLE `billiongoods`.`store_order`
ADD COLUMN `exceptedResume` DATETIME NULL DEFAULT NULL
AFTER `internationalTracking`,
ADD COLUMN `refundId` VARCHAR(45) NULL DEFAULT NULL
AFTER `exceptedResume`;

ALTER TABLE `billiongoods`.`store_order`
CHANGE COLUMN `refundId` `refundToken` VARCHAR(45) NULL DEFAULT NULL;

# 10.14
ALTER TABLE `billiongoods`.`account_personality`
CHANGE COLUMN `lastActivityDate` `lastActivity` DATETIME NULL;

# 10.11
ALTER TABLE `billiongoods`.`store_attribute`
ADD COLUMN `priority` TINYINT NULL DEFAULT 0
AFTER `type`;

UPDATE store_order
SET state=state - 1
WHERE state > 1;

# 10.10
ALTER TABLE `billiongoods`.`store_group`
ADD COLUMN `categoryId` INT NULL
AFTER `name`,
ADD INDEX `category_foreign_idx` (`categoryId` ASC);

ALTER TABLE `billiongoods`.`store_group`
ADD CONSTRAINT `category_foreign`
FOREIGN KEY (`categoryId`)
REFERENCES `billiongoods`.`store_category` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `billiongoods`.`store_product_property`
CHANGE COLUMN `dvalue` `bvalue` INT(1) NULL DEFAULT NULL;

# 9.10
ALTER TABLE `billiongoods`.`store_product`
CHANGE COLUMN `stockSold` `soldCount` INT(11) NOT NULL DEFAULT '0',
CHANGE COLUMN `stockAvailable` `stockLeftovers` INT(11) NULL DEFAULT NULL,
CHANGE COLUMN `restockDate` `stockRestockDate` DATE NULL DEFAULT NULL;

# 8.10
CREATE TABLE `billiongoods`.`service_validation` (
  `id`        INT            NOT NULL AUTO_INCREMENT,
  `productId` INT            NOT NULL,
  `timestamp` DATETIME       NOT NULL,
  `op`        DECIMAL(10, 4) NULL,
  `opp`       DECIMAL(10, 4) NULL,
  `osp`       DECIMAL(10, 4) NULL,
  `ospp`      DECIMAL(10, 4) NULL,
  `np`        DECIMAL(10, 4) NULL,
  `npp`       DECIMAL(10, 4) NULL,
  `nsp`       DECIMAL(10, 4) NULL,
  `nspp`      DECIMAL(10, 4) NULL,
  `oa`        INT            NULL,
  `ord`       DATE           NULL,
  `na`        INT            NULL,
  `nrd`       DATE           NULL,
  `errMsg`    VARCHAR(255)   NULL,
  PRIMARY KEY (`id`));

