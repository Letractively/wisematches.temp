-- MySQL dump 10.13  Distrib 5.5.9, for Win32 (x86)
--
-- Host: localhost    Database: billiongoods
-- ------------------------------------------------------
-- Server version	5.5.9

/*!40101 SET @OLD_CHARACTER_SET_CLIENT = @@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS = @@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION = @@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE = @@TIME_ZONE */;
/*!40103 SET TIME_ZONE = '+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS = @@UNIQUE_CHECKS, UNIQUE_CHECKS = 0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS = 0 */;
/*!40101 SET @OLD_SQL_MODE = @@SQL_MODE, SQL_MODE = 'NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES = @@SQL_NOTES, SQL_NOTES = 0 */;

--
-- Current Database: `billiongoods`
--

CREATE DATABASE /*!32312 IF NOT EXISTS */ `billiongoods` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `billiongoods`;

--
-- Table structure for table `account_blacknames`
--

DROP TABLE IF EXISTS `account_blacknames`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `account_blacknames` (
  `username` VARCHAR(100) NOT NULL,
  `reason`   VARCHAR(255) NOT NULL,
  PRIMARY KEY (`username`)
)
  ENGINE =InnoDB
  DEFAULT CHARSET =utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account_blacknames`
--

LOCK TABLES `account_blacknames` WRITE;
/*!40000 ALTER TABLE `account_blacknames` DISABLE KEYS */;
/*!40000 ALTER TABLE `account_blacknames` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `account_lock`
--

DROP TABLE IF EXISTS `account_lock`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `account_lock` (
  `playerId`      BIGINT(20)   NOT NULL,
  `publicReason`  VARCHAR(145) NOT NULL,
  `privateReason` VARCHAR(145) NOT NULL,
  `lockDate`      DATETIME     NOT NULL,
  `unlockDate`    DATETIME     NOT NULL,
  PRIMARY KEY (`playerId`),
  UNIQUE KEY `userId_UNIQUE` (`playerId`),
  KEY `fk_user_lock_user_player` (`playerId`),
  CONSTRAINT `fk_user_lock_user_player` FOREIGN KEY (`playerId`) REFERENCES `account_personality` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
)
  ENGINE =InnoDB
  DEFAULT CHARSET =utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account_lock`
--

LOCK TABLES `account_lock` WRITE;
/*!40000 ALTER TABLE `account_lock` DISABLE KEYS */;
/*!40000 ALTER TABLE `account_lock` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `account_membership`
--

DROP TABLE IF EXISTS `account_membership`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `account_membership` (
  `pid`        BIGINT(20) NOT NULL,
  `membership` INT(11)    NOT NULL,
  `expiration` DATE       NOT NULL,
  PRIMARY KEY (`pid`)
)
  ENGINE =InnoDB
  DEFAULT CHARSET =utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account_membership`
--

LOCK TABLES `account_membership` WRITE;
/*!40000 ALTER TABLE `account_membership` DISABLE KEYS */;
/*!40000 ALTER TABLE `account_membership` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `account_personality`
--

DROP TABLE IF EXISTS `account_personality`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `account_personality` (
  `id`       BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(100) NOT NULL,
  `password` VARCHAR(100) NOT NULL,
  `email`    VARCHAR(150) NOT NULL,
  `language` CHAR(2)      NOT NULL,
  `timezone` VARCHAR(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `username_UNIQUE` (`username`),
  UNIQUE KEY `email_UNIQUE` (`email`)
)
  ENGINE =InnoDB
  DEFAULT CHARSET =utf8
  COMMENT ='The base table that contains information about a player';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account_personality`
--

LOCK TABLES `account_personality` WRITE;
/*!40000 ALTER TABLE `account_personality` DISABLE KEYS */;
/*!40000 ALTER TABLE `account_personality` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `account_recovery`
--

DROP TABLE IF EXISTS `account_recovery`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `account_recovery` (
  `account`   BIGINT(20)  NOT NULL,
  `token`     VARCHAR(45) NOT NULL,
  `generated` DATETIME    NOT NULL,
  PRIMARY KEY (`account`)
)
  ENGINE =InnoDB
  DEFAULT CHARSET =utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account_recovery`
--

LOCK TABLES `account_recovery` WRITE;
/*!40000 ALTER TABLE `account_recovery` DISABLE KEYS */;
/*!40000 ALTER TABLE `account_recovery` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notification_settings`
--

DROP TABLE IF EXISTS `notification_settings`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `notification_settings` (
  `pid`                                       BIGINT(20) NOT NULL,
  `playground.game.started`                   TINYINT(4) DEFAULT NULL,
  `playground.game.turn`                      TINYINT(4) DEFAULT NULL,
  `playground.game.finished`                  TINYINT(4) DEFAULT NULL,
  `playground.game.expiration.day`            TINYINT(4) DEFAULT NULL,
  `playground.game.expiration.half`           TINYINT(4) DEFAULT NULL,
  `playground.game.expiration.hour`           TINYINT(4) DEFAULT NULL,
  `playground.challenge.initiated`            TINYINT(4) DEFAULT NULL,
  `playground.challenge.finalized.rejected`   TINYINT(4) DEFAULT NULL,
  `playground.challenge.finalized.repudiated` TINYINT(4) DEFAULT NULL,
  `playground.challenge.finalized.terminated` TINYINT(4) DEFAULT NULL,
  `playground.challenge.expiration.days`      TINYINT(4) DEFAULT NULL,
  `playground.challenge.expiration.day`       TINYINT(4) DEFAULT NULL,
  `playground.message.received`               TINYINT(4) DEFAULT NULL,
  `playground.tourney.announced`              TINYINT(4) DEFAULT NULL,
  `playground.tourney.finished`               TINYINT(4) DEFAULT NULL,
  `playground.dictionary.approved`            TINYINT(4) DEFAULT '0',
  `playground.dictionary.rejected`            TINYINT(4) DEFAULT '0',
  `playground.award.granted`                  TINYINT(4) DEFAULT '0',
  PRIMARY KEY (`pid`)
)
  ENGINE =InnoDB
  DEFAULT CHARSET =utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notification_settings`
--

LOCK TABLES `notification_settings` WRITE;
/*!40000 ALTER TABLE `notification_settings` DISABLE KEYS */;
/*!40000 ALTER TABLE `notification_settings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notification_timestamp`
--

DROP TABLE IF EXISTS `notification_timestamp`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `notification_timestamp` (
  `pid`                                       BIGINT(20) NOT NULL,
  `playground.game.started`                   DATETIME DEFAULT NULL,
  `playground.game.turn`                      DATETIME DEFAULT NULL,
  `playground.game.finished`                  DATETIME DEFAULT NULL,
  `playground.game.expiration.day`            DATETIME DEFAULT NULL,
  `playground.game.expiration.half`           DATETIME DEFAULT NULL,
  `playground.game.expiration.hour`           DATETIME DEFAULT NULL,
  `playground.challenge.initiated`            DATETIME DEFAULT NULL,
  `playground.challenge.finalized.rejected`   DATETIME DEFAULT NULL,
  `playground.challenge.finalized.repudiated` DATETIME DEFAULT NULL,
  `playground.challenge.finalized.terminated` DATETIME DEFAULT NULL,
  `playground.challenge.expiration.days`      DATETIME DEFAULT NULL,
  `playground.challenge.expiration.day`       DATETIME DEFAULT NULL,
  `playground.message.received`               DATETIME DEFAULT NULL,
  `playground.tourney.announced`              DATETIME DEFAULT NULL,
  `playground.tourney.finished`               DATETIME DEFAULT NULL,
  PRIMARY KEY (`pid`)
)
  ENGINE =InnoDB
  DEFAULT CHARSET =utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notification_timestamp`
--

LOCK TABLES `notification_timestamp` WRITE;
/*!40000 ALTER TABLE `notification_timestamp` DISABLE KEYS */;
/*!40000 ALTER TABLE `notification_timestamp` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `persistent_logins`
--

DROP TABLE IF EXISTS `persistent_logins`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `persistent_logins` (
  `series`    VARCHAR(64)  NOT NULL,
  `username`  VARCHAR(150) NOT NULL,
  `token`     VARCHAR(64)  NOT NULL,
  `last_used` TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`series`)
)
  ENGINE =InnoDB
  DEFAULT CHARSET =utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `persistent_logins`
--

LOCK TABLES `persistent_logins` WRITE;
/*!40000 ALTER TABLE `persistent_logins` DISABLE KEYS */;
/*!40000 ALTER TABLE `persistent_logins` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `player_activity`
--

DROP TABLE IF EXISTS `player_activity`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `player_activity` (
  `pid`                 BIGINT(20) NOT NULL,
  `last_messages_check` TIMESTAMP  NULL DEFAULT CURRENT_TIMESTAMP,
  `last_activity`       TIMESTAMP  NULL DEFAULT NULL,
  PRIMARY KEY (`pid`)
)
  ENGINE =InnoDB
  DEFAULT CHARSET =utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `player_activity`
--

LOCK TABLES `player_activity` WRITE;
/*!40000 ALTER TABLE `player_activity` DISABLE KEYS */;
/*!40000 ALTER TABLE `player_activity` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `player_notification`
--

DROP TABLE IF EXISTS `player_notification`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `player_notification` (
  `pid`                                  BIGINT(20) NOT NULL,
  `playground.game.started`              DATETIME DEFAULT NULL,
  `playground.game.turn`                 DATETIME DEFAULT NULL,
  `playground.game.finished`             DATETIME DEFAULT NULL,
  `playground.game.expiration.day`       DATETIME DEFAULT NULL,
  `playground.game.expiration.half`      DATETIME DEFAULT NULL,
  `playground.game.expiration.hour`      DATETIME DEFAULT NULL,
  `playground.challenge.initiated`       DATETIME DEFAULT NULL,
  `playground.challenge.rejected`        DATETIME DEFAULT NULL,
  `playground.challenge.repudiated`      DATETIME DEFAULT NULL,
  `playground.challenge.terminated`      DATETIME DEFAULT NULL,
  `playground.challenge.expiration.days` DATETIME DEFAULT NULL,
  `playground.challenge.expiration.day`  DATETIME DEFAULT NULL,
  `playground.message.received`          DATETIME DEFAULT NULL,
  `playground.tourney.announced`         DATETIME DEFAULT NULL,
  PRIMARY KEY (`pid`)
)
  ENGINE =InnoDB
  DEFAULT CHARSET =utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `player_notification`
--

LOCK TABLES `player_notification` WRITE;
/*!40000 ALTER TABLE `player_notification` DISABLE KEYS */;
/*!40000 ALTER TABLE `player_notification` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `store_article`
--

DROP TABLE IF EXISTS `store_article`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `store_article` (
  `id`                 INT(11)      NOT NULL AUTO_INCREMENT,
  `name`               VARCHAR(100) NOT NULL,
  `description`        TEXT         NOT NULL,
  `active`             TINYINT(4) DEFAULT NULL,
  `categoryId`         INT(11)      NOT NULL,
  `price`              FLOAT        NOT NULL,
  `primordialPrice`    INT(11) DEFAULT NULL,
  `restockDate`        DATE DEFAULT NULL,
  `registrationDate`   DATE         NOT NULL,
  `soldCount`          INT(11) DEFAULT '0',
  `buyPrice`           FLOAT        NOT NULL,
  `buyPrimordialPrice` FLOAT DEFAULT NULL,
  `referenceId`        VARCHAR(50) DEFAULT NULL,
  `referenceCode`      VARCHAR(50) DEFAULT NULL,
  `wholesaler`         TINYINT(4) DEFAULT NULL,
  `validationDate`     DATETIME DEFAULT NULL,
  `previewImageId`     VARCHAR(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
)
  ENGINE =InnoDB
  AUTO_INCREMENT =2
  DEFAULT CHARSET =utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `store_article`
--

LOCK TABLES `store_article` WRITE;
/*!40000 ALTER TABLE `store_article` DISABLE KEYS */;
INSERT INTO `store_article` VALUES (1, 'df', 'sdf', 1, 11, 12.5, NULL, NULL, '2013-07-22', 0, 12.4, NULL, 'sdf', 'sdf', 0, NULL, NULL);
/*!40000 ALTER TABLE `store_article` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `store_article_accessory`
--

DROP TABLE IF EXISTS `store_article_accessory`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `store_article_accessory` (
  `id`          INT(11)    NOT NULL AUTO_INCREMENT,
  `articleId`   INT(11)    NOT NULL,
  `accessoryId` INT(11)    NOT NULL,
  `order`       TINYINT(4) NOT NULL,
  PRIMARY KEY (`id`)
)
  ENGINE =InnoDB
  DEFAULT CHARSET =utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `store_article_accessory`
--

LOCK TABLES `store_article_accessory` WRITE;
/*!40000 ALTER TABLE `store_article_accessory` DISABLE KEYS */;
/*!40000 ALTER TABLE `store_article_accessory` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `store_article_image`
--

DROP TABLE IF EXISTS `store_article_image`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `store_article_image` (
  `id`        INT(11)     NOT NULL AUTO_INCREMENT,
  `articleId` INT(11)     NOT NULL,
  `imageId`   VARCHAR(45) NOT NULL,
  `order`     TINYINT(4)  NOT NULL,
  PRIMARY KEY (`id`)
)
  ENGINE =InnoDB
  DEFAULT CHARSET =utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `store_article_image`
--

LOCK TABLES `store_article_image` WRITE;
/*!40000 ALTER TABLE `store_article_image` DISABLE KEYS */;
/*!40000 ALTER TABLE `store_article_image` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `store_article_option`
--

DROP TABLE IF EXISTS `store_article_option`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `store_article_option` (
  `id`          INT(11)    NOT NULL AUTO_INCREMENT,
  `articleId`   INT(11)    NOT NULL,
  `attributeId` INT(11)    NOT NULL,
  `value`       VARCHAR(45) DEFAULT NULL,
  `order`       TINYINT(4) NOT NULL,
  PRIMARY KEY (`id`)
)
  ENGINE =InnoDB
  AUTO_INCREMENT =5
  DEFAULT CHARSET =utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `store_article_option`
--

LOCK TABLES `store_article_option` WRITE;
/*!40000 ALTER TABLE `store_article_option` DISABLE KEYS */;
INSERT INTO `store_article_option` VALUES (3, 1, 3, 'красный', 0), (4, 1, 3, 'синий', 1);
/*!40000 ALTER TABLE `store_article_option` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `store_article_property`
--

DROP TABLE IF EXISTS `store_article_property`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `store_article_property` (
  `id`          INT(11)    NOT NULL AUTO_INCREMENT,
  `articleId`   INT(11)    NOT NULL,
  `attributeId` INT(11)    NOT NULL,
  `value`       VARCHAR(45) DEFAULT NULL,
  `order`       TINYINT(4) NOT NULL,
  PRIMARY KEY (`id`)
)
  ENGINE =InnoDB
  AUTO_INCREMENT =3
  DEFAULT CHARSET =utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `store_article_property`
--

LOCK TABLES `store_article_property` WRITE;
/*!40000 ALTER TABLE `store_article_property` DISABLE KEYS */;
INSERT INTO `store_article_property` VALUES (1, 1, 4, 'test', 0), (2, 1, 3, 'test2', 1);
/*!40000 ALTER TABLE `store_article_property` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `store_attribute`
--

DROP TABLE IF EXISTS `store_attribute`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `store_attribute` (
  `id`   INT(11)     NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(20) NOT NULL,
  `unit` VARCHAR(10) DEFAULT NULL,
  PRIMARY KEY (`id`)
)
  ENGINE =InnoDB
  AUTO_INCREMENT =20
  DEFAULT CHARSET =utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `store_attribute`
--

LOCK TABLES `store_attribute` WRITE;
/*!40000 ALTER TABLE `store_attribute` DISABLE KEYS */;
INSERT INTO `store_attribute` VALUES (2, 'время', 'секунды'), (3, 'цвет', ''), (4, 'время полета', '');
/*!40000 ALTER TABLE `store_attribute` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `store_category`
--

DROP TABLE IF EXISTS `store_category`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `store_category` (
  `id`          INT(11)    NOT NULL AUTO_INCREMENT,
  `name`        VARCHAR(100) DEFAULT NULL,
  `description` TEXT,
  `parent`      INT(11) DEFAULT NULL,
  `position`    TINYINT(4) NOT NULL,
  `active`      INT(1) DEFAULT '0',
  PRIMARY KEY (`id`)
)
  ENGINE =InnoDB
  AUTO_INCREMENT =45
  DEFAULT CHARSET =utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `store_category`
--

LOCK TABLES `store_category` WRITE;
/*!40000 ALTER TABLE `store_category` DISABLE KEYS */;
INSERT INTO `store_category` VALUES (1, 'Игрушки и хобби', 'vfgdsf g sdg dsgf dsgf dsfg dsfg dsfg dsfg dsg dsgf df', NULL, 1, 1), (2, 'Радиоправляемы модели', NULL, 1, 0, 1), (3, 'Куклы и мягкие игрушки', NULL, 1, 1, 1), (4, 'Волшебные кубики', NULL, 1, 2, 1), (5, 'На солнечных батареях', NULL, 1, 3, 1), (6, 'Музыкальные инструменты', NULL, 1, 4, 1), (7, 'Развлечения и шутки', NULL, 1, 5, 1), (8, 'Развивающие игры', NULL, 1, 6, 1), (9, 'Праздники и события', NULL, 1, 7, 1), (10, 'Гаджет Игрушки', NULL, 1, 8, 1), (11, 'Вертолеты', NULL, 2, 0, 1), (12, 'Квадрокоптер', NULL, 2, 1, 1), (13, 'Самолеты', NULL, 2, 2, 1), (14, 'Машины', NULL, 2, 3, 1), (15, 'Лодки', NULL, 2, 4, 1), (16, 'Запчасти для вертолетов', NULL, 2, 5, 1), (17, 'Запчасти для квадрокоптеров', NULL, 2, 6, 1), (18, 'Запчасти для самолетов', NULL, 2, 7, 1), (19, 'Запчасти для машин', NULL, 2, 8, 1), (20, 'Аксессуары Apple', NULL, NULL, 0, 1), (21, 'Аксессуары iPhone', NULL, 20, 0, 1), (22, 'Аксессуары iPad', NULL, 20, 1, 1), (23, 'Аксессуары iPod', NULL, 20, 2, 1), (24, 'Аксессуары Mac', NULL, 20, 3, 1), (25, 'Запасные части', NULL, 21, 0, 1), (26, 'Защита экрана', NULL, 21, 1, 1), (27, 'Автопринадлежности', NULL, 21, 2, 1), (28, 'Зарядные устройства', NULL, 21, 3, 1), (29, 'Докстанции и кабели', NULL, 21, 4, 1), (30, 'Работа с SIM картами', NULL, 21, 5, 1), (31, 'Стилусы', NULL, 21, 6, 1), (32, 'Чехлы', NULL, 21, 7, 1), (33, 'Динамики', NULL, 21, 8, 1), (34, 'Наушники', NULL, 21, 9, 1), (35, 'Пылезащитные аксессуары', NULL, 21, 10, 1), (36, 'Игровые контроллеры', NULL, 21, 11, 1), (37, 'Держатели', NULL, 21, 12, 1), (38, 'Гаджеты', NULL, 21, 13, 1), (39, 'Активные перчатки', NULL, 21, 14, 1);
/*!40000 ALTER TABLE `store_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `store_category_attribute`
--

DROP TABLE IF EXISTS `store_category_attribute`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `store_category_attribute` (
  `id`          INT(11) NOT NULL AUTO_INCREMENT,
  `categoryId`  INT(11) NOT NULL,
  `attributeId` INT(11) NOT NULL,
  PRIMARY KEY (`id`)
)
  ENGINE =InnoDB
  DEFAULT CHARSET =utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `store_category_attribute`
--

LOCK TABLES `store_category_attribute` WRITE;
/*!40000 ALTER TABLE `store_category_attribute` DISABLE KEYS */;
/*!40000 ALTER TABLE `store_category_attribute` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE = @OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE = @OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS = @OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS = @OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT = @OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS = @OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION = @OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES = @OLD_SQL_NOTES */;

-- Dump completed on 2013-07-29 23:08:53
