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
  `nickname` VARCHAR(100) NOT NULL,
  `password` VARCHAR(100) NOT NULL,
  `email`    VARCHAR(150) NOT NULL,
  `language` CHAR(2)      NOT NULL,
  `timezone` VARCHAR(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `username_UNIQUE` (`nickname`),
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
-- Table structure for table `store_category`
--

DROP TABLE IF EXISTS `store_category`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `store_category` (
  `id`       INT(11)    NOT NULL AUTO_INCREMENT,
  `name`     VARCHAR(100) DEFAULT NULL,
  `parent`   INT(11) DEFAULT NULL,
  `position` TINYINT(4) NOT NULL,
  PRIMARY KEY (`id`)
)
  ENGINE =InnoDB
  AUTO_INCREMENT =65
  DEFAULT CHARSET =utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `store_category`
--

LOCK TABLES `store_category` WRITE;
/*!40000 ALTER TABLE `store_category` DISABLE KEYS */;
INSERT INTO `store_category` VALUES (1, 'Игрушки и хобби', NULL, 1), (2, 'Радиоуправляемы модели', 1, 0), (3, 'Куклы и мягкие игрушки', 1, 1), (4, 'Волшебные кубики', 1, 2), (5, 'На солнечных батареях', 1, 3), (6, 'Музыкальные инструменты', 1, 4), (7, 'Развлечения и шутки', 1, 5), (8, 'Развивающие игры', 1, 6), (9, 'Праздники и события', 1, 7), (10, 'Гаджет Игрушки', 1, 8), (11, 'Вертолеты', 2, 0), (12, 'Квадрокоптер', 2, 1), (13, 'Самолеты', 2, 2), (14, 'Машины', 2, 3), (15, 'Лодки', 2, 4), (16, 'Запчасти для вертолетов', 2, 5), (17, 'Запчасти для квадрокоптеров', 2, 6), (18, 'Запчасти для самолетов', 2, 7), (19, 'Запчасти для машин', 2, 8), (20, 'Аксессуары Apple', NULL, 0), (21, 'Аксессуары iPhone', 20, 0), (22, 'Аксессуары iPad', 20, 1), (23, 'Аксессуары iPod', 20, 2), (24, 'Аксессуары Mac', 20, 3), (25, 'Запасные части', 21, 0), (26, 'Защита экрана', 21, 1), (27, 'Автопринадлежности', 21, 2), (28, 'Зарядные устройства', 21, 3), (29, 'Докстанции и кабели', 21, 4), (30, 'Работа с SIM картами', 21, 5), (31, 'Стилусы', 21, 6), (32, 'Чехлы', 21, 7), (33, 'Динамики', 21, 8), (34, 'Наушники', 21, 9), (35, 'Пылезащитные аксессуары', 21, 10), (36, 'Игровые контроллеры', 21, 11), (37, 'Держатели', 21, 12), (38, 'Гаджеты', 21, 13), (39, 'Активные перчатки', 21, 14);
/*!40000 ALTER TABLE `store_category` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE = @OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE = @OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS = @OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS = @OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT = @OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS = @OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION = @OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES = @OLD_SQL_NOTES */;

-- Dump completed on 2013-07-22 23:53:02
