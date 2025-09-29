-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Nov 10, 2024 at 05:13 PM
-- Server version: 10.4.28-MariaDB
-- PHP Version: 8.2.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";

 /*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
 /*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
 /*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
 /*!40101 SET NAMES utf8mb4 */;

--
-- Database: `cafe`
--
CREATE DATABASE IF NOT EXISTS `cafe` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `cafe`;

-- --------------------------------------------------------
-- Table structure for table `admin`
-- --------------------------------------------------------
CREATE TABLE `admin` (
  `id` int(4) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `password` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO `admin` (`id`, `name`, `password`) VALUES
(1, 'admin', '1234');

-- --------------------------------------------------------
-- Table structure for table `customers`
-- --------------------------------------------------------
CREATE TABLE `customers` (
  `cus_id` varchar(10) NOT NULL,
  `name` varchar(500) NOT NULL,
  `address` varchar(500) NOT NULL,
  `contact_no` varchar(10) NOT NULL,
  PRIMARY KEY (`cus_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO `customers` (`cus_id`, `name`, `address`, `contact_no`) VALUES
('C0001', 'Lalithra', '141/6,Galle', '0912345678'),
('C0002', 'Shakila', 'Karapitiya', '0712345675'),
('C0003', 'Kavindi', 'Kurunegala', '0789867567'),
('C0004', 'Shashith', 'Anuradhapura', '0701234567');

-- --------------------------------------------------------
-- Table structure for table `employees`
-- --------------------------------------------------------
CREATE TABLE `employees` (
  `emp_id` varchar(11) NOT NULL,
  `name` varchar(20) NOT NULL,
  `password` varchar(10) NOT NULL,
  `first_name` varchar(50) NOT NULL,
  `last_name` varchar(50) NOT NULL,
  `contact_number` varchar(15) DEFAULT NULL,
  PRIMARY KEY (`emp_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO `employees` (`emp_id`, `name`, `password`, `first_name`, `last_name`, `contact_number`) VALUES
('E001', 'bavi', '123', 'bavindu', 'Shamen', '0704830888'),
('E002', 'kavi', '1234', 'Kavindi', 'Basnayaka', '0711234567'),
('E003', 'lali', '1234', 'lalithra', 'indupa', '1234567890');

-- --------------------------------------------------------
-- Table structure for table `products`
-- --------------------------------------------------------
CREATE TABLE `products` (
  `product_id` varchar(11) NOT NULL,
  `product_name` varchar(20) NOT NULL,
  `product_type` varchar(20) NOT NULL,
  `stock` int(11) NOT NULL,
  `price` float NOT NULL,
  `image` longblob DEFAULT NULL,
  PRIMARY KEY (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO `products` (`product_id`, `product_name`, `product_type`, `stock`, `price`, `image`) VALUES
('P001', 'Big Mac', 'Meals', 7, 1200, NULL),
('P002', 'French Fries', 'Snacks', 14, 950, NULL),
('P007', 'Big Mac', 'Meals', 7, 1200, NULL);


-- --------------------------------------------------------
-- Table structure for table `sales`
-- --------------------------------------------------------
CREATE TABLE `sales` (
  `sale_Id` varchar(5) NOT NULL,
  `customer_name` varchar(30) NOT NULL,
  `customer_contact_no` varchar(10) NOT NULL,
  `total` float NOT NULL,
  `date` date NOT NULL,
  PRIMARY KEY (`sale_Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO `sales` (`sale_Id`, `customer_name`, `customer_contact_no`, `total`, `date`) VALUES
('S001', 'Shakila', '0712345675', 1000, '2024-11-09'),
('S002', 'Shakila', '0712345675', 2000, '2024-11-09'),
('S003', 'Lalithra', '0912345678', 1000, '2024-11-09'),
('S005', 'Shakila', '0712345675', 1000, '2024-11-09'),
('S007', 'Kavindi', '0789867567', 1000, '2024-11-09'),
('S008', 'Lalithra', '0912345678', 1000, '2024-11-09'),
('S009', 'Shakila', '0712345675', 1000, '2024-11-09'),
('S010', 'Kavindi', '0789867567', 1000, '2024-11-09'),
('S011', 'Shakila', '0712345675', 620, '2024-11-10'),
('S012', 'Shakila', '0712345675', 640, '2024-11-10'),
('S013', 'Shakila', '0712345675', 1700, '2024-11-10'),
('S014', 'Shakila', '0712345675', 3000, '2024-11-10');

COMMIT;

 /*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
 /*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
 /*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
