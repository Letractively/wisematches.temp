ALTER TABLE `billiongoods`.`account_role`
ADD CONSTRAINT `fk_account_role_account_personality`
FOREIGN KEY (`account`)
REFERENCES `billiongoods`.`account_personality` (`id`)
  ON DELETE CASCADE
  ON UPDATE NO ACTION;

ALTER TABLE `billiongoods`.`store_order_item`
DROP COLUMN `name`;

ALTER TABLE `billiongoods`.`store_product_property`
CHANGE COLUMN `value` `svalue` VARCHAR(45) NULL DEFAULT NULL,
ADD COLUMN `ivalue` INT NULL
AFTER `svalue`,
ADD COLUMN `dvalue` DOUBLE NULL
AFTER `ivalue`;