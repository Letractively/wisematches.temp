DROP TABLE IF EXISTS `billiongoods`.`store_category_parameter`;

CREATE TABLE IF NOT EXISTS `billiongoods`.`store_category_parameter` (
  `id`          INT(11) NOT NULL AUTO_INCREMENT,
  `categoryId`  INT(11) NOT NULL,
  `attributeId` INT(11) NOT NULL,
  INDEX `category_idx` (`categoryId` ASC),
  INDEX `attribute_idx` (`attributeId` ASC),
  PRIMARY KEY (`id`),
  CONSTRAINT `category`
  FOREIGN KEY (`categoryId`)
  REFERENCES `billiongoods`.`store_category` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `attribute`
  FOREIGN KEY (`attributeId`)
  REFERENCES `billiongoods`.`store_attribute` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8
  COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `billiongoods`.`store_category_parameter_value` (
  `id`          INT(11)     NOT NULL AUTO_INCREMENT,
  `parameterId` INT(11)     NOT NULL,
  `value`       VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `parameter_idx` (`parameterId` ASC),
  CONSTRAINT `parameter`
  FOREIGN KEY (`parameterId`)
  REFERENCES `billiongoods`.`store_category_parameter` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8
  COLLATE = utf8_general_ci;

INSERT INTO store_category_parameter (categoryId, attributeId) SELECT
                                                                 categoryId,
                                                                 attributeId
                                                               FROM store_category_attribute;

ALTER TABLE store_category_parameter MODIFY `categoryId` INT(11) NULL DEFAULT NULL;