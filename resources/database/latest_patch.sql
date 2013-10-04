CREATE TABLE `billiongoods`.`store_product_tracking` (
  `id`           INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `registration` DATETIME     NOT NULL,
  `productId`    INT          NOT NULL,
  `type`         INT(2)       NOT NULL,
  `personId`     BIGINT(20)   NULL,
  `personEmail`  VARCHAR(145) NULL,
  PRIMARY KEY (`id`),
  INDEX `email_index` (`personEmail` ASC),
  INDEX `product_foreign_idx` (`productId` ASC),
  INDEX `person_foreign_idx` (`personId` ASC),
  CONSTRAINT `product_foreign`
  FOREIGN KEY (`productId`)
  REFERENCES `billiongoods`.`store_product` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `person_foreign`
  FOREIGN KEY (`personId`)
  REFERENCES `billiongoods`.`account_personality` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION);
