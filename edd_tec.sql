-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: May 10, 2025 at 02:22 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `edd_tec`
--

-- --------------------------------------------------------

--
-- Table structure for table `customers`
--

CREATE TABLE `customers` (
  `customer_id` int(11) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `address` text DEFAULT NULL,
  `is_registered` tinyint(1) DEFAULT 1,
  `is_flagged` tinyint(1) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `customers`
--

INSERT INTO `customers` (`customer_id`, `user_id`, `address`, `is_registered`, `is_flagged`) VALUES
(1, 4, '123 Main St, Cityville', 1, 0),
(2, 5, '456 Oak Ave, Townsville', 1, 0),
(3, 6, '343 Queen St, Zivie', 1, 0),
(4, 7, 'Neistat Apt. NYC.', 1, 0),
(5, 8, 'Ansh G Apt. NYC.', 1, 0),
(6, 9, 'John H asdfgh', 1, 0),
(7, 10, 'Jack gsdgsda', 1, 0),
(8, 11, 'Oak st. apt-1 400142.', 1, 0),
(9, 12, '11, Witchurch, CF24144', 1, 0);

-- --------------------------------------------------------

--
-- Table structure for table `equipment`
--

CREATE TABLE `equipment` (
  `equipment_id` int(11) NOT NULL,
  `customer_id` int(11) NOT NULL,
  `type` varchar(100) NOT NULL,
  `brand` varchar(100) NOT NULL,
  `model` varchar(100) NOT NULL,
  `serial_number` varchar(100) DEFAULT NULL,
  `problem_description` text NOT NULL,
  `registration_date` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `equipment`
--

INSERT INTO `equipment` (`equipment_id`, `customer_id`, `type`, `brand`, `model`, `serial_number`, `problem_description`, `registration_date`) VALUES
(1, 1, 'Laptop', 'HP', '00111', '123456789131', 'Screen Broken', '2025-04-27 23:04:24'),
(2, 2, 'Mobile', 'Apple', '12', '123352324', 'Not Charging', '2025-04-27 23:13:04'),
(3, 1, 'TV', 'SONY', 'B123', '8484', 'HDMI No Connection', '2025-04-27 23:18:18'),
(4, 1, 'TV', 'SAMSUNG', 'SS-324', '4235', 'No Sound', '2025-04-27 23:36:58'),
(5, 2, 'Laptop', 'SONY', 'T2342', '634623', 'Mousepad Not Working', '2025-04-27 23:52:19'),
(6, 3, 'TV', 'LG', 'LL-352', '35324', 'Reverse Curent', '2025-04-28 00:09:37'),
(7, 4, 'Laptop', 'Dell', 'XPS', '4236452', 'Display Dosent WORK', '2025-05-06 01:32:42'),
(8, 5, 'TV', 'LG', 'G23214', '143556', 'HDMI input doesn\'t work', '2025-05-06 01:44:22'),
(9, 7, 'Mobile', 'Apple', '12 pro ', '21t4224', 'Sim slot doesnt work.', '2025-05-06 01:50:22'),
(10, 8, 'Phone', 'Samsung', 'S24', '6217462178', 'Speaker Dosent Work', '2025-05-06 13:13:06'),
(11, 5, 'Phone', 'One Plus', '13R', '132341', 'Power Button Issue', '2025-05-08 23:27:35'),
(12, 6, 'TV', 'Haier', '1231dsa', '353232', 'Heating Issue', '2025-05-08 23:44:36'),
(13, 8, 'Laptop', 'Asus', 'ROG16', '21314166', 'RAM Change', '2025-05-08 23:45:35');

-- --------------------------------------------------------

--
-- Table structure for table `jobs`
--

CREATE TABLE `jobs` (
  `job_id` int(11) NOT NULL,
  `equipment_id` int(11) NOT NULL,
  `technician_id` int(11) NOT NULL,
  `admin_id` int(11) NOT NULL,
  `status` enum('Job Created','Job Assessed','Repair In Progress','Ready for Collection','Completed') DEFAULT 'Job Created',
  `assessment_notes` text DEFAULT NULL,
  `repair_tasks` text DEFAULT NULL,
  `total_cost` decimal(10,2) DEFAULT 0.00,
  `created_date` timestamp NOT NULL DEFAULT current_timestamp(),
  `completed_date` timestamp NULL DEFAULT NULL,
  `labor_cost` decimal(10,2) DEFAULT 0.00,
  `service_charge` decimal(10,2) DEFAULT 0.00,
  `discount` decimal(10,2) DEFAULT 0.00,
  `cost_notes` text DEFAULT NULL,
  `cost_calculated_by` int(11) DEFAULT NULL,
  `cost_calculated_date` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `jobs`
--

INSERT INTO `jobs` (`job_id`, `equipment_id`, `technician_id`, `admin_id`, `status`, `assessment_notes`, `repair_tasks`, `total_cost`, `created_date`, `completed_date`, `labor_cost`, `service_charge`, `discount`, `cost_notes`, `cost_calculated_by`, `cost_calculated_date`) VALUES
(1, 1, 2, 1, 'Ready for Collection', 'Replace Screen', NULL, 70.00, '2025-04-27 23:06:41', NULL, 0.00, 0.00, 0.00, NULL, NULL, NULL),
(2, 2, 3, 1, 'Ready for Collection', 'Change Socket', NULL, 83.00, '2025-04-27 23:13:59', NULL, 0.00, 0.00, 0.00, NULL, NULL, NULL),
(3, 3, 2, 1, 'Ready for Collection', 'Change HDMI Input Pin', NULL, 105.32, '2025-04-27 23:18:27', NULL, 0.00, 0.00, 0.00, NULL, NULL, NULL),
(4, 4, 3, 1, 'Ready for Collection', 'Change Speaker', NULL, 170.23, '2025-04-27 23:37:07', NULL, 0.00, 0.00, 0.00, NULL, NULL, NULL),
(5, 5, 2, 1, 'Ready for Collection', 'Replace Moucepad', NULL, 137.11, '2025-04-27 23:52:32', NULL, 0.00, 0.00, 0.00, NULL, NULL, NULL),
(6, 6, 3, 1, 'Ready for Collection', 'Motherboard Replacement', NULL, 223.20, '2025-04-28 00:09:48', NULL, 50.00, 90.00, 10.00, NULL, 1, '2025-04-28 00:13:26'),
(7, 7, 2, 1, 'Job Assessed', 'Replacement', NULL, 0.00, '2025-05-06 01:34:34', NULL, 0.00, 0.00, 0.00, NULL, NULL, NULL),
(8, 9, 3, 1, 'Ready for Collection', 'replce sim slot', NULL, 179.00, '2025-05-06 01:51:26', NULL, 50.00, 40.00, 5.00, NULL, 1, '2025-05-06 01:54:10'),
(9, 10, 2, 1, 'Ready for Collection', 'Replace', NULL, 183.00, '2025-05-06 13:14:03', NULL, 50.00, 30.00, 5.00, NULL, 1, '2025-05-06 13:17:28'),
(10, 8, 2, 1, 'Job Assessed', 'Change', NULL, 0.00, '2025-05-08 19:49:35', NULL, 0.00, 0.00, 0.00, NULL, NULL, NULL),
(11, 11, 2, 1, 'Ready for Collection', 'Power Button Holder and the button is Cracked.', NULL, 130.00, '2025-05-08 23:30:13', NULL, 50.00, 20.00, 0.00, NULL, 1, '2025-05-09 18:13:53'),
(12, 13, 2, 1, 'Job Created', NULL, NULL, 0.00, '2025-05-09 17:51:12', NULL, 0.00, 0.00, 0.00, NULL, NULL, NULL),
(13, 12, 2, 1, 'Job Created', NULL, NULL, 0.00, '2025-05-09 17:51:17', NULL, 0.00, 0.00, 0.00, NULL, NULL, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `job_cost_history`
--

CREATE TABLE `job_cost_history` (
  `history_id` int(11) NOT NULL,
  `job_id` int(11) NOT NULL,
  `changed_by` int(11) NOT NULL,
  `change_date` timestamp NOT NULL DEFAULT current_timestamp(),
  `old_total` decimal(10,2) DEFAULT NULL,
  `new_total` decimal(10,2) DEFAULT NULL,
  `notes` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `job_cost_history`
--

INSERT INTO `job_cost_history` (`history_id`, `job_id`, `changed_by`, `change_date`, `old_total`, `new_total`, `notes`) VALUES
(1, 6, 1, '2025-04-28 00:13:26', 223.20, 223.20, 'Initial cost calculation'),
(2, 8, 1, '2025-05-06 01:54:10', 179.00, 179.00, 'Initial cost calculation'),
(3, 9, 1, '2025-05-06 13:17:28', 183.00, 183.00, 'Initial cost calculation'),
(4, 11, 1, '2025-05-09 18:13:53', 130.00, 130.00, 'Initial cost calculation');

-- --------------------------------------------------------

--
-- Table structure for table `notifications`
--

CREATE TABLE `notifications` (
  `notification_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `message` text NOT NULL,
  `is_read` tinyint(1) DEFAULT 0,
  `created_date` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `notifications`
--

INSERT INTO `notifications` (`notification_id`, `user_id`, `message`, `is_read`, `created_date`) VALUES
(1, 4, 'Your equipment repair is ready for collection. Total cost: $170.23000000000002', 1, '2025-04-27 23:39:27'),
(2, 5, 'Your repair is ready for collection.\n\nItemized Costs:\n- Parts: $42.11\n- Labor: $50.00\n- Service Charge: $50.00\n- Discount: -$5.00\nTotal: $137.11', 1, '2025-04-27 23:57:11'),
(3, 6, 'Your repair is ready for collection.\n\nItemized Costs:\n- Parts: $93.20\n- Labor: $50.00\n- Service Charge: $90.00\n- Discount: -$10.00\nTotal: $223.20', 1, '2025-04-28 00:13:26'),
(4, 4, 'Promotion: OFFER\n\n20% OFF on Mobile Repairs.', 1, '2025-04-28 00:14:53'),
(5, 5, 'Promotion: OFFER\n\n20% OFF on Mobile Repairs.', 1, '2025-04-28 00:14:53'),
(6, 6, 'Promotion: OFFER\n\n20% OFF on Mobile Repairs.', 1, '2025-04-28 00:14:53'),
(7, 10, 'Your repair is ready for collection.\n\nItemized Costs:\n- Parts: $94.00\n- Labor: $50.00\n- Service Charge: $40.00\n- Discount: -$5.00\nTotal: $179.00', 1, '2025-05-06 01:54:10'),
(8, 4, 'Promotion: Discount 20%\n\nCOde: DISCOUNT', 1, '2025-05-06 01:55:14'),
(9, 5, 'Promotion: Discount 20%\n\nCOde: DISCOUNT', 0, '2025-05-06 01:55:14'),
(10, 6, 'Promotion: Discount 20%\n\nCOde: DISCOUNT', 0, '2025-05-06 01:55:14'),
(11, 7, 'Promotion: Discount 20%\n\nCOde: DISCOUNT', 0, '2025-05-06 01:55:14'),
(12, 8, 'Promotion: Discount 20%\n\nCOde: DISCOUNT', 1, '2025-05-06 01:55:14'),
(13, 9, 'Promotion: Discount 20%\n\nCOde: DISCOUNT', 0, '2025-05-06 01:55:14'),
(14, 10, 'Promotion: Discount 20%\n\nCOde: DISCOUNT', 1, '2025-05-06 01:55:14'),
(15, 4, 'Promotion: Offer\n\nBuy 1 Get 1 Free Phone Case', 1, '2025-05-06 13:15:15'),
(16, 5, 'Promotion: Offer\n\nBuy 1 Get 1 Free Phone Case', 0, '2025-05-06 13:15:15'),
(17, 6, 'Promotion: Offer\n\nBuy 1 Get 1 Free Phone Case', 0, '2025-05-06 13:15:15'),
(18, 7, 'Promotion: Offer\n\nBuy 1 Get 1 Free Phone Case', 0, '2025-05-06 13:15:15'),
(19, 8, 'Promotion: Offer\n\nBuy 1 Get 1 Free Phone Case', 1, '2025-05-06 13:15:15'),
(20, 9, 'Promotion: Offer\n\nBuy 1 Get 1 Free Phone Case', 0, '2025-05-06 13:15:15'),
(21, 10, 'Promotion: Offer\n\nBuy 1 Get 1 Free Phone Case', 0, '2025-05-06 13:15:15'),
(22, 11, 'Promotion: Offer\n\nBuy 1 Get 1 Free Phone Case', 1, '2025-05-06 13:15:15'),
(23, 11, 'Your repair is ready for collection.\n\nItemized Costs:\n- Parts: $108.00\n- Labor: $50.00\n- Service Charge: $30.00\n- Discount: -$5.00\nTotal: $183.00', 1, '2025-05-06 13:17:28'),
(24, 8, 'Your repair is ready for collection.\n\nItemized Costs:\n- Parts: $60.00\n- Labor: $50.00\n- Service Charge: $20.00\n- Discount: -$0.00\nTotal: $130.00', 1, '2025-05-09 18:13:53'),
(25, 4, 'Promotion: OFFER\n\n50% OFF ON SCREEN PROTECTORS', 0, '2025-05-09 18:19:04'),
(26, 5, 'Promotion: OFFER\n\n50% OFF ON SCREEN PROTECTORS', 0, '2025-05-09 18:19:04'),
(27, 6, 'Promotion: OFFER\n\n50% OFF ON SCREEN PROTECTORS', 0, '2025-05-09 18:19:04'),
(28, 7, 'Promotion: OFFER\n\n50% OFF ON SCREEN PROTECTORS', 0, '2025-05-09 18:19:04'),
(29, 8, 'Promotion: OFFER\n\n50% OFF ON SCREEN PROTECTORS', 1, '2025-05-09 18:19:04'),
(30, 9, 'Promotion: OFFER\n\n50% OFF ON SCREEN PROTECTORS', 0, '2025-05-09 18:19:04'),
(31, 10, 'Promotion: OFFER\n\n50% OFF ON SCREEN PROTECTORS', 0, '2025-05-09 18:19:04'),
(32, 11, 'Promotion: OFFER\n\n50% OFF ON SCREEN PROTECTORS', 0, '2025-05-09 18:19:04'),
(33, 12, 'Promotion: OFFER\n\n50% OFF ON SCREEN PROTECTORS', 1, '2025-05-09 18:19:04');

-- --------------------------------------------------------

--
-- Table structure for table `parts`
--

CREATE TABLE `parts` (
  `part_id` int(11) NOT NULL,
  `job_id` int(11) NOT NULL,
  `supplier_id` int(11) DEFAULT NULL,
  `name` varchar(100) NOT NULL,
  `description` text DEFAULT NULL,
  `quantity` int(11) NOT NULL DEFAULT 1,
  `unit_cost` decimal(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `parts`
--

INSERT INTO `parts` (`part_id`, `job_id`, `supplier_id`, `name`, `description`, `quantity`, `unit_cost`) VALUES
(1, 1, 1, 'HP 00111 Screen', 'Screen', 1, 20.00),
(2, 2, 2, 'Socket12', 'Socket12', 1, 33.00),
(3, 3, 1, 'SONY HDMI PIN', 'SONY HDMI PIN', 1, 55.32),
(4, 4, 1, 'Speaker', 'Speaker 216317', 1, 120.23),
(5, 5, 2, 'Mousepad', 'Mousepad', 1, 42.11),
(6, 6, 1, 'Motherboard', 'LG TV Motherboard LL-324', 1, 93.20),
(7, 7, 2, 'Dell Display', 'Dell Display', 1, 213.00),
(8, 8, 2, 'sim slot cover', 'sim slot cover', 2, 47.00),
(9, 9, 2, 'S24 Speaker', 'S24 Speaker', 2, 54.00),
(10, 11, 2, 'S24 Button Holder', 'Button Holder', 1, 24.00),
(11, 11, 1, 'S24 Button', 'S24 Button', 1, 12.00),
(12, 10, 2, 'Hdmi Connecter', 'Hdmi Connecter', 1, 12.00),
(13, 11, 2, 'S24 Button Holder', 'Button Holder', 1, 24.00),
(14, 10, 2, 'HDMI Cables', 'HDMI Cables', 2, 8.00);

-- --------------------------------------------------------

--
-- Table structure for table `promotions`
--

CREATE TABLE `promotions` (
  `promotion_id` int(11) NOT NULL,
  `title` varchar(100) NOT NULL,
  `description` text NOT NULL,
  `start_date` date NOT NULL,
  `end_date` date NOT NULL,
  `created_by` int(11) NOT NULL,
  `created_date` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `promotions`
--

INSERT INTO `promotions` (`promotion_id`, `title`, `description`, `start_date`, `end_date`, `created_by`, `created_date`) VALUES
(1, 'OFFER', '20% OFF on Mobile Repairs.', '2025-04-28', '2025-04-30', 1, '2025-04-28 00:14:43'),
(2, 'Discount 20%', 'COde: DISCOUNT', '2025-05-06', '2025-05-08', 1, '2025-05-06 01:55:05'),
(3, 'Offer', 'Buy 1 Get 1 Free Phone Case', '2025-05-06', '2025-05-10', 1, '2025-05-06 13:15:08'),
(4, 'OFFER', '50% OFF ON SCREEN PROTECTORS', '2025-05-09', '2025-05-12', 1, '2025-05-09 18:18:43');

-- --------------------------------------------------------

--
-- Table structure for table `suppliers`
--

CREATE TABLE `suppliers` (
  `supplier_id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `contact_person` varchar(100) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `phone` varchar(20) NOT NULL,
  `address` text NOT NULL,
  `specialization` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `suppliers`
--

INSERT INTO `suppliers` (`supplier_id`, `name`, `contact_person`, `email`, `phone`, `address`, `specialization`) VALUES
(1, 'TechParts Inc', 'Mike Supplier', 'mike@techparts.com', '5551234567', '789 Industrial Zone, Cityville', 'Electronics Components'),
(2, 'Gadget Supplies', 'Sarah Johnson', 'sarah@gadgets.com', '5557654321', '321 Business Park, Townsville', 'Device Parts');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `user_id` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `full_name` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `user_type` enum('admin','technician','customer') NOT NULL,
  `registration_date` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`user_id`, `username`, `password`, `full_name`, `email`, `phone`, `user_type`, `registration_date`) VALUES
(1, 'admin', 'admin123', 'System Administrator', 'admin@eddtech.com', '1234567890', 'admin', '2025-04-27 22:51:19'),
(2, 'tech1', 'tech123', 'John Technician', 'tech1@eddtech.com', '1234567891', 'technician', '2025-04-27 22:51:19'),
(3, 'tech2', 'tech123', 'Jane Technician', 'tech2@eddtech.com', '1234567892', 'technician', '2025-04-27 22:51:19'),
(4, 'customer1', 'customer123', 'Alice Customer', 'customer1@example.com', '1234567893', 'customer', '2025-04-27 22:51:19'),
(5, 'customer2', 'customer123', 'Bob Customer', 'customer2@example.com', '1234567894', 'customer', '2025-04-27 22:51:19'),
(6, 'customer3', 'customer123', 'Jack Miller', 'jackm@example.com', '1234567895', 'customer', '2025-04-28 00:04:31'),
(7, 'Customer6', 'Customer123', 'Casey Neistat', 'casey@gmail.com', '62761377317', 'customer', '2025-05-06 01:31:18'),
(8, 'customer7', 'customer123', 'Ansh G', 'ansh@gmail.com', '36213723127', 'customer', '2025-05-06 01:42:58'),
(9, 'cust8', 'cust124', 'John H', 'john@gmail.com', '274652364527', 'customer', '2025-05-06 01:46:34'),
(10, 'cust9', 'cust123', 'Jack k', 'jack@ksfs.com', '2142352144', 'customer', '2025-05-06 01:49:16'),
(11, 'cust10', 'cust123', 'Darren Watkins', 'darren@gmail.com', '1234554321', 'customer', '2025-05-06 13:11:52'),
(12, 'customer11', 'customer123', 'Matt Watson', 'matt@gmail.com', '2134124412', 'customer', '2025-05-09 17:49:43');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `customers`
--
ALTER TABLE `customers`
  ADD PRIMARY KEY (`customer_id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `equipment`
--
ALTER TABLE `equipment`
  ADD PRIMARY KEY (`equipment_id`),
  ADD KEY `customer_id` (`customer_id`);

--
-- Indexes for table `jobs`
--
ALTER TABLE `jobs`
  ADD PRIMARY KEY (`job_id`),
  ADD KEY `equipment_id` (`equipment_id`),
  ADD KEY `technician_id` (`technician_id`),
  ADD KEY `admin_id` (`admin_id`),
  ADD KEY `cost_calculated_by` (`cost_calculated_by`);

--
-- Indexes for table `job_cost_history`
--
ALTER TABLE `job_cost_history`
  ADD PRIMARY KEY (`history_id`),
  ADD KEY `job_id` (`job_id`),
  ADD KEY `changed_by` (`changed_by`);

--
-- Indexes for table `notifications`
--
ALTER TABLE `notifications`
  ADD PRIMARY KEY (`notification_id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `parts`
--
ALTER TABLE `parts`
  ADD PRIMARY KEY (`part_id`),
  ADD KEY `job_id` (`job_id`),
  ADD KEY `supplier_id` (`supplier_id`);

--
-- Indexes for table `promotions`
--
ALTER TABLE `promotions`
  ADD PRIMARY KEY (`promotion_id`),
  ADD KEY `created_by` (`created_by`);

--
-- Indexes for table `suppliers`
--
ALTER TABLE `suppliers`
  ADD PRIMARY KEY (`supplier_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`user_id`),
  ADD UNIQUE KEY `username` (`username`),
  ADD UNIQUE KEY `email` (`email`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `customers`
--
ALTER TABLE `customers`
  MODIFY `customer_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT for table `equipment`
--
ALTER TABLE `equipment`
  MODIFY `equipment_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=14;

--
-- AUTO_INCREMENT for table `jobs`
--
ALTER TABLE `jobs`
  MODIFY `job_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=14;

--
-- AUTO_INCREMENT for table `job_cost_history`
--
ALTER TABLE `job_cost_history`
  MODIFY `history_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `notifications`
--
ALTER TABLE `notifications`
  MODIFY `notification_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=34;

--
-- AUTO_INCREMENT for table `parts`
--
ALTER TABLE `parts`
  MODIFY `part_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- AUTO_INCREMENT for table `promotions`
--
ALTER TABLE `promotions`
  MODIFY `promotion_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `suppliers`
--
ALTER TABLE `suppliers`
  MODIFY `supplier_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `customers`
--
ALTER TABLE `customers`
  ADD CONSTRAINT `customers_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE;

--
-- Constraints for table `equipment`
--
ALTER TABLE `equipment`
  ADD CONSTRAINT `equipment_ibfk_1` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`) ON DELETE CASCADE;

--
-- Constraints for table `jobs`
--
ALTER TABLE `jobs`
  ADD CONSTRAINT `jobs_ibfk_1` FOREIGN KEY (`equipment_id`) REFERENCES `equipment` (`equipment_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `jobs_ibfk_2` FOREIGN KEY (`technician_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `jobs_ibfk_3` FOREIGN KEY (`admin_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `jobs_ibfk_4` FOREIGN KEY (`cost_calculated_by`) REFERENCES `users` (`user_id`) ON DELETE SET NULL;

--
-- Constraints for table `job_cost_history`
--
ALTER TABLE `job_cost_history`
  ADD CONSTRAINT `job_cost_history_ibfk_1` FOREIGN KEY (`job_id`) REFERENCES `jobs` (`job_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `job_cost_history_ibfk_2` FOREIGN KEY (`changed_by`) REFERENCES `users` (`user_id`) ON DELETE CASCADE;

--
-- Constraints for table `notifications`
--
ALTER TABLE `notifications`
  ADD CONSTRAINT `notifications_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE;

--
-- Constraints for table `parts`
--
ALTER TABLE `parts`
  ADD CONSTRAINT `parts_ibfk_1` FOREIGN KEY (`job_id`) REFERENCES `jobs` (`job_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `parts_ibfk_2` FOREIGN KEY (`supplier_id`) REFERENCES `suppliers` (`supplier_id`) ON DELETE SET NULL;

--
-- Constraints for table `promotions`
--
ALTER TABLE `promotions`
  ADD CONSTRAINT `promotions_ibfk_1` FOREIGN KEY (`created_by`) REFERENCES `users` (`user_id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
