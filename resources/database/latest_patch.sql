SET @OLD_UNIQUE_CHECKS = @@UNIQUE_CHECKS, UNIQUE_CHECKS = 0;
SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS = 0;
SET @OLD_SQL_MODE = @@SQL_MODE, SQL_MODE = 'TRADITIONAL';

CREATE TABLE IF NOT EXISTS `billiongoods`.`account_role` (
  `pid`  BIGINT(20)  NOT NULL,
  `role` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`pid`))
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8
  COLLATE = utf8_general_ci;

ALTER TABLE `billiongoods`.`account_personality` CHANGE COLUMN `nickname` `username` VARCHAR(100)
CHARACTER SET 'utf8'
COLLATE 'utf8_general_ci' NOT NULL
, DROP INDEX `username_UNIQUE`;

ALTER TABLE `billiongoods`.`account_personality` DROP COLUMN `username`, ADD COLUMN `username` VARCHAR(100)
CHARACTER SET 'utf8'
COLLATE 'utf8_general_ci' NOT NULL
AFTER `id`;

ALTER TABLE `billiongoods`.`store_article` ADD COLUMN `weight` FLOAT(11) NULL DEFAULT NULL
AFTER `categoryId`, CHANGE COLUMN `active` `active` TINYINT(4) NULL DEFAULT NULL
AFTER `previewImageId`;

ALTER TABLE `billiongoods`.`account_personality` DROP COLUMN `username`, ADD COLUMN `username` VARCHAR(100)
CHARACTER SET 'utf8'
COLLATE 'utf8_general_ci' NOT NULL
AFTER `id`;

CREATE TABLE IF NOT EXISTS `billiongoods`.`store_order` (
  `id`            BIGINT(20)   NOT NULL,
  `buyer`         BIGINT(20)   NULL DEFAULT NULL,
  `code`          VARCHAR(45)  NULL DEFAULT NULL,
  `addressName`   VARCHAR(120) NULL DEFAULT NULL,
  `addressRegion` VARCHAR(120) NULL DEFAULT NULL,
  `addressCity`   VARCHAR(120) NULL DEFAULT NULL,
  `addressPostal` VARCHAR(10)  NULL DEFAULT NULL,
  `addressStreet` VARCHAR(250) NULL DEFAULT NULL,
  `state`         INT(11)      NULL DEFAULT NULL,
  PRIMARY KEY (`id`))
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8
  COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `billiongoods`.`store_order_item` (
  `order`    BIGINT(20)   NOT NULL,
  `number`   INT(11)      NOT NULL,
  `code`     VARCHAR(12)  NULL DEFAULT NULL,
  `name`     VARCHAR(255) NULL DEFAULT NULL,
  `quantity` INT(11)      NULL DEFAULT NULL,
  `amount`   FLOAT(11)    NULL DEFAULT NULL,
  `weight`   FLOAT(11)    NULL DEFAULT NULL,
  `options`  VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`order`, `number`))
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8
  COLLATE = utf8_general_ci;

ALTER TABLE `billiongoods`.`store_order` CHANGE COLUMN `id` `id` BIGINT(20) NOT NULL AUTO_INCREMENT;

UPDATE billiongoods.store_article
SET weight=0;

ALTER TABLE `billiongoods`.`store_order_item` CHANGE COLUMN `order` `orderId` BIGINT(20) NOT NULL
, DROP PRIMARY KEY
, ADD PRIMARY KEY (`orderId`, `number`);

CREATE TABLE IF NOT EXISTS `billiongoods`.`store_order_response` (
  `id`                 BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `order`              BIGINT(20)   NULL DEFAULT NULL,
  `ack`                VARCHAR(45)  NULL DEFAULT NULL,
  `timestamp`          DATETIME     NULL DEFAULT NULL,
  `correlationId`      VARCHAR(45)  NULL DEFAULT NULL,
  `version`            VARCHAR(10)  NULL DEFAULT NULL,
  `build`              VARCHAR(20)  NULL DEFAULT NULL,
  `token`              VARCHAR(45)  NULL DEFAULT NULL,
  `payerEmail`         VARCHAR(145) NULL DEFAULT NULL,
  `payerID`            VARCHAR(45)  NULL DEFAULT NULL,
  `payerStatus`        VARCHAR(45)  NULL DEFAULT NULL,
  `payerNameFirst`     VARCHAR(145) NULL DEFAULT NULL,
  `payerNameLast`      VARCHAR(145) NULL DEFAULT NULL,
  `payerNameMiddle`    VARCHAR(145) NULL DEFAULT NULL,
  `payerCountry`       CHAR(2)      NULL DEFAULT NULL,
  `payerBusiness`      VARCHAR(255) NULL DEFAULT NULL,
  `payerPhone`         VARCHAR(50)  NULL DEFAULT NULL,
  `payerEnchancedInfo` VARCHAR(255) NULL DEFAULT NULL,
  `custom`             VARCHAR(255) NULL DEFAULT NULL,
  `invoiceId`          VARCHAR(255) NULL DEFAULT NULL,
  `contactPhone`       VARCHAR(45)  NULL DEFAULT NULL,
  `soap`               LONGTEXT     NULL DEFAULT NULL,
  PRIMARY KEY (`id`))
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8
  COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `billiongoods`.`store_order_response_error` (
  `responceId`   BIGINT(20)   NOT NULL,
  `code`         VARCHAR(20)  NULL DEFAULT NULL,
  `severity`     VARCHAR(20)  NULL DEFAULT NULL,
  `shortMessage` VARCHAR(255) NULL DEFAULT NULL,
  `longMessage`  TEXT         NULL DEFAULT NULL,
  `parameters`   VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`responceId`))
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8
  COLLATE = utf8_general_ci;

ALTER TABLE `billiongoods`.`store_order_response` RENAME TO `billiongoods`.`paypal_response`;

ALTER TABLE `billiongoods`.`store_order_response_error` RENAME TO `billiongoods`.`paypal_error`;

CREATE TABLE IF NOT EXISTS `billiongoods`.`paypal_transaction` (
  `id` BIGINT(20) NOT NULL,
  PRIMARY KEY (`id`))
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8
  COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `billiongoods`.`paypal_ipn_message` (
  `id`                  BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `txn_id`              VARCHAR(245) NULL DEFAULT NULL,
  `txn_type`            VARCHAR(245) NULL DEFAULT NULL,
  `verify_sign`         VARCHAR(245) NULL DEFAULT NULL,
  `business`            VARCHAR(245) NULL DEFAULT NULL,
  `charset`             VARCHAR(245) NULL DEFAULT NULL,
  `custom`              VARCHAR(245) NULL DEFAULT NULL,
  `ipn_track_id`        VARCHAR(245) NULL DEFAULT NULL,
  `notify_version`      VARCHAR(245) NULL DEFAULT NULL,
  `parent_txn_id`       VARCHAR(245) NULL DEFAULT NULL,
  `receipt_id`          VARCHAR(245) NULL DEFAULT NULL,
  `receiver_email`      VARCHAR(245) NULL DEFAULT NULL,
  `receiver_id`         VARCHAR(245) NULL DEFAULT NULL,
  `resend`              VARCHAR(245) NULL DEFAULT NULL,
  `residence_country`   VARCHAR(245) NULL DEFAULT NULL,
  `test_ipn`            VARCHAR(245) NULL DEFAULT NULL,
  `transaction_subject` VARCHAR(245) NULL DEFAULT NULL,
  `message`             MEDIUMTEXT   NULL DEFAULT NULL,
  PRIMARY KEY (`id`))
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8
  COLLATE = utf8_general_ci;

SET SQL_MODE = @OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS = @OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS = @OLD_UNIQUE_CHECKS;
