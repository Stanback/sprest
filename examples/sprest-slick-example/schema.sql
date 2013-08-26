delimiter $$

CREATE DATABASE `reactive-example` /*!40100 DEFAULT CHARACTER SET utf8 */$$

CREATE TABLE `reminder` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `remindAt` datetime NOT NULL,
  `title` varchar(255) NOT NULL,
  `body` text NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8$$

CREATE TABLE `todo` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `text` text NOT NULL,
  `done` tinyint(1) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8$$
