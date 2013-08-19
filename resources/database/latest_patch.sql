ALTER TABLE `billiongoods`.`account_role`
CHANGE COLUMN `role` `role` VARCHAR(15) NOT NULL,
DROP PRIMARY KEY,
ADD PRIMARY KEY (`pid`, `role`);

CREATE TABLE IF NOT EXISTS `billiongoods`.`store_group` (
  `id`   INT(11)      NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(145) NULL DEFAULT NULL,
  PRIMARY KEY (`id`))
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8
  COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `billiongoods`.`store_group_item` (
  `groupId`   INT(11) NOT NULL,
  `articleId` INT(11) NOT NULL,
  PRIMARY KEY (`groupId`, `articleId`))
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8
  COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `billiongoods`.`store_article_relationship` (
  `articleId` INT(11)     NOT NULL,
  `groupId`   INT(11)     NOT NULL,
  `type`      SMALLINT(6) NULL DEFAULT NULL,
  PRIMARY KEY (`articleId`, `groupId`))
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8
  COLLATE = utf8_general_ci;
