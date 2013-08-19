ALTER TABLE `billiongoods`.`account_role`
CHANGE COLUMN `role` `role` VARCHAR(15) NOT NULL,
DROP PRIMARY KEY,
ADD PRIMARY KEY (`pid`, `role`);

