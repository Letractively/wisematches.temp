#12.09
ALTER TABLE `billiongoods`.`account_personality`
CHANGE COLUMN `email` `email` VARCHAR(150) NULL
AFTER `id`,
CHANGE COLUMN `password` `password` VARCHAR(100) NULL DEFAULT NULL
AFTER `email`,
ADD COLUMN `language` TINYINT NULL
AFTER `username`,
ADD COLUMN `timezone` VARCHAR(245) NULL
AFTER `language`;

ALTER TABLE `billiongoods`.`account_personality`
CHANGE COLUMN `language` `language` CHAR(2) NULL DEFAULT 'RU',
CHANGE COLUMN `timezone` `timezone` VARCHAR(245) NULL DEFAULT 'GMT+00:00';

UPDATE account_personality
SET language = 'RU', timezone = 'GMT+00:00';

ALTER TABLE `billiongoods`.`account_personality`
CHANGE COLUMN `language` `language` CHAR(2) NOT NULL DEFAULT 'RU',
CHANGE COLUMN `timezone` `timezone` VARCHAR(245) NOT NULL DEFAULT 'GMT+00:00';

INSERT INTO `billiongoods`.`system_version` (`version`) VALUES ('1209');

#12.03
ALTER TABLE `billiongoods`.`store_coupon`
CHANGE COLUMN `reference` `reference` INT(11) NULL DEFAULT NULL;

INSERT INTO `billiongoods`.`system_version` (`version`) VALUES ('1203');

#11.26
ALTER TABLE `billiongoods`.`store_order`
ADD COLUMN `shipped` DATETIME NULL
AFTER `created`;

UPDATE store_order o LEFT JOIN store_order_log l
    ON o.id = l.orderId
SET o.shipped = l.timestamp
WHERE l.orderState = 5;

INSERT INTO `billiongoods`.`system_version` (`version`) VALUES ('1126');

#11.25
ALTER TABLE `billiongoods`.`account_personality`
CHANGE COLUMN `password` `password` VARCHAR(100) NULL,
CHANGE COLUMN `email` `email` VARCHAR(150) NULL;

CREATE TABLE account_UserConnection (userId         VARCHAR(255) NOT NULL,
                                     providerId     VARCHAR(255) NOT NULL,
                                     providerUserId VARCHAR(255),
                                     rank           INT          NOT NULL,
                                     displayName    VARCHAR(255),
                                     profileUrl     VARCHAR(512),
                                     imageUrl       VARCHAR(512),
                                     accessToken    VARCHAR(255) NOT NULL,
                                     secret         VARCHAR(255),
                                     refreshToken   VARCHAR(255),
                                     expireTime     BIGINT,
  PRIMARY KEY (userId, providerId, providerUserId));
CREATE UNIQUE INDEX UserConnectionRank ON account_UserConnection (userId, providerId, rank);

INSERT INTO `billiongoods`.`system_version` (`version`) VALUES ('1125');

#11.12
ALTER TABLE `billiongoods`.`store_order_log`
DROP COLUMN `code`;

INSERT INTO `billiongoods`.`system_version` (`version`) VALUES ('1112');
#11.01
UPDATE store_product_relationship
SET type = 0;

ALTER TABLE `billiongoods`.`store_group`
ADD COLUMN `type` INT(2) NOT NULL DEFAULT 0
AFTER `name`;

INSERT INTO `billiongoods`.`system_version` (`version`) VALUES ('1101');

#10.31
ALTER TABLE `billiongoods`.`store_basket`
ADD COLUMN `coupon` INT(11) NULL
AFTER `updatingTime`;

ALTER TABLE `billiongoods`.`store_order`
ADD COLUMN `discount` DECIMAL(10, 4) NOT NULL DEFAULT 0
AFTER `amount`,
ADD COLUMN `coupon` INT NULL DEFAULT NULL
AFTER `shipmentType`;

ALTER TABLE `billiongoods`.`store_coupon`
DROP COLUMN `id`,
DROP PRIMARY KEY,
ADD PRIMARY KEY (`code`),
DROP INDEX `code_INDEX`,
DROP INDEX `code_UNIQUE`;

ALTER TABLE `billiongoods`.`store_order`
CHANGE COLUMN `coupon` `coupon` VARCHAR(10) NULL DEFAULT NULL;

ALTER TABLE `billiongoods`.`store_basket`
CHANGE COLUMN `coupon` `coupon` VARCHAR(10) NULL DEFAULT NULL;

INSERT INTO `billiongoods`.`system_version` (`version`) VALUES ('1031');

#10.30
ALTER TABLE `billiongoods`.`store_coupon`
CHANGE COLUMN `closure` `closure` DATETIME NULL DEFAULT NULL,
ADD COLUMN `lastUsed` DATETIME NULL DEFAULT NULL
AFTER `closure`;

ALTER TABLE `billiongoods`.`store_coupon`
DROP COLUMN `lastUsed`,
DROP COLUMN `finished`,
DROP COLUMN `started`,
CHANGE COLUMN `created` `creation` DATETIME NOT NULL,
CHANGE COLUMN `amount` `termination` DATETIME NOT NULL,
CHANGE COLUMN `couponType` `amountType` TINYINT(4) NOT NULL,
CHANGE COLUMN `referenceId` `reference` INT(11) NOT NULL,
CHANGE COLUMN `scheduledCount` `utilizedCount` SMALLINT(6) NULL DEFAULT '0',
CHANGE COLUMN `remainingCount` `allocatedCount` SMALLINT(6) NULL DEFAULT '0',
CHANGE COLUMN `closure` `lastUtilization` DATETIME NULL DEFAULT NULL,
ADD INDEX `reference_index` (`reference` ASC),
ADD INDEX `reference_type_index` (`referenceType` ASC);

ALTER TABLE `billiongoods`.`store_coupon`
ADD COLUMN `amount` DOUBLE NOT NULL
AFTER `termination`;

ALTER TABLE `billiongoods`.`store_coupon`
CHANGE COLUMN `termination` `termination` DATETIME NULL;

CREATE TABLE `billiongoods`.`system_version` (
  `version`      INT      NOT NULL,
  `modification` DATETIME NOT NULL);

ALTER TABLE `billiongoods`.`system_version`
ADD PRIMARY KEY (`version`);

ALTER TABLE `billiongoods`.`system_version`
CHANGE COLUMN `modification` `modification` TIMESTAMP NOT NULL;

INSERT INTO `billiongoods`.`system_version` (`version`) VALUES ('1030');

#10.28
ALTER TABLE `billiongoods`.`store_order_item`
DROP FOREIGN KEY `fk_store_order_item_store_order1`;
ALTER TABLE `billiongoods`.`store_order_item`
ADD CONSTRAINT `fk_store_order_item_store_order1`
FOREIGN KEY (`orderId`)
REFERENCES `billiongoods`.`store_order` (`id`)
  ON DELETE CASCADE
  ON UPDATE NO ACTION;


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
SET state = state - 1
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
SET state = state - 1
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

