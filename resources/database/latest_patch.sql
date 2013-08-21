ALTER TABLE `billiongoods`.`store_article`
CHANGE COLUMN `description` `description` LONGTEXT NOT NULL,
CHANGE COLUMN `primordialPrice` `primordialPrice` DOUBLE NULL DEFAULT NULL;

CREATE TABLE IF NOT EXISTS `billiongoods`.`service_price_renewal` (
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

DROP TABLE IF EXISTS `billiongoods`.`service_price_validation`;