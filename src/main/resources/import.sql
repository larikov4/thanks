-- phpMyAdmin SQL Dump
-- version 4.0.10.12
-- http://www.phpmyadmin.net
--
-- Хост: 127.7.21.2:3306
-- Час створення: Лют 23 2016 р., 16:47
-- Версія сервера: 5.5.45
-- Версія PHP: 5.3.3

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- База даних: `thanks`
--

-- --------------------------------------------------------

--
-- Структура таблиці `city`
--

CREATE TABLE IF NOT EXISTS `city` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=3 ;

--
-- Дамп даних таблиці `city`
--

INSERT INTO `city` (`id`, `name`) VALUES
(1, 'Kharkiv'),
(2, 'city2'),
(3, 'city3'),
(4, 'city4'),
(5, 'city5'),
(6, '�������');

-- --------------------------------------------------------

--
-- Структура таблиці `equipment`
--

CREATE TABLE IF NOT EXISTS `equipment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=2 ;

--
-- Дамп даних таблиці `equipment`
--

INSERT INTO `equipment` (`id`, `name`) VALUES
(1, 'equipment1'),
(2, 'equipment2'),
(3, 'equipment3'),
(4, 'equipment4'),
(5, 'equipment5');

-- --------------------------------------------------------

--
-- Структура таблиці `location`
--

CREATE TABLE IF NOT EXISTS `location` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=2 ;

--
-- Дамп даних таблиці `location`
--

INSERT INTO `location` (`id`, `name`) VALUES
(1, 'location1'),
(2, 'location2'),
(3, 'location3'),
(4, 'location4'),
(5, 'location5');

-- --------------------------------------------------------

--
-- Структура таблиці `user`
--

CREATE TABLE IF NOT EXISTS `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `email` varchar(255) NOT NULL,
  `is_admin` bit(1) NOT NULL,
  `name` varchar(255) NOT NULL,
  `surname` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=3 ;

--
-- Дамп даних таблиці `user`
--

INSERT INTO `user` (`id`, `email`, `is_admin`, `name`, `surname`) VALUES
(1, 'user1@email.com', b'1', 'User1N', 'User1S'),
(3, 'user3@email.com', b'1', 'User3N', 'User3S'),
(4, 'user4@email.com', b'1', 'User4N', 'User4S'),
(5, 'user5@email.com', b'1', 'User5N', 'User5S'),
(2, 'user2@email.com', b'0', 'User2N', 'User2S');

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
