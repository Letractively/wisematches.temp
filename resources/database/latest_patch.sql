ALTER TABLE `billiongoods`.`store_article`
CHANGE COLUMN `registrationDate` `registrationDate` DATETIME NOT NULL;

CREATE TABLE IF NOT EXISTS `billiongoods`.`service_exchange` (
  `id`           INT(11)  NOT NULL AUTO_INCREMENT,
  `timestamp`    DATETIME NULL DEFAULT NULL,
  `exchangeRate` DOUBLE   NULL DEFAULT NULL,
  PRIMARY KEY (`id`))
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8
  COLLATE = utf8_general_ci;

ALTER TABLE `billiongoods`.`store_article_relationship`
CHANGE COLUMN `groupId` `groupId` INT(11) NOT NULL
AFTER `articleId`,
DROP PRIMARY KEY,
ADD PRIMARY KEY (`articleId`, `groupId`, `type`);

CREATE TABLE IF NOT EXISTS `billiongoods`.`service_price_validation` (
  `id`                         INT(11)  NOT NULL AUTO_INCREMENT,
  `articleId`                  INT(11)  NULL DEFAULT NULL,
  `timestamp`                  DATETIME NULL DEFAULT NULL,
  `oldPrice`                   DOUBLE   NULL DEFAULT NULL,
  `oldPrimordialPrice`         DOUBLE   NULL DEFAULT NULL,
  `oldSupplierPrice`           DOUBLE   NULL DEFAULT NULL,
  `oldSupplierPrimordialPrice` DOUBLE   NULL DEFAULT NULL,
  `newPrice`                   DOUBLE   NULL DEFAULT NULL,
  `newPrimordialPrice`         DOUBLE   NULL DEFAULT NULL,
  `newSupplierPrice`           DOUBLE   NULL DEFAULT NULL,
  `newSupplierPrimordialPrice` DOUBLE   NULL DEFAULT NULL,
  PRIMARY KEY (`id`))
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8
  COLLATE = utf8_general_ci;

DROP TABLE IF EXISTS `billiongoods`.`store_exchange`;