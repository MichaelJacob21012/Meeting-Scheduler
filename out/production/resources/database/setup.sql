-- phpMyAdmin SQL Dump
-- version 4.8.5
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Apr 20, 2020 at 01:34 PM
-- Server version: 10.1.38-MariaDB
-- PHP Version: 7.3.2

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `individual_project`
--

-- --------------------------------------------------------

--
-- Table structure for table `account`
--

CREATE TABLE `account` (
`accountID` int(9) NOT NULL,
`firstName` varchar(30) NOT NULL,
`surname` varchar(30) NOT NULL,
`email` varchar(50) NOT NULL,
`password` varbinary(255) NOT NULL,
`salt` varbinary(255) NOT NULL,
`isAdmin` tinyint(1) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


-- --------------------------------------------------------

--
-- Table structure for table `accountattendee`
--

CREATE TABLE `accountattendee` (
`attendeeID` int(9) NOT NULL,
`accountID` int(9) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


-- --------------------------------------------------------

--
-- Table structure for table `attendee`
--

CREATE TABLE `attendee` (
`attendeeID` int(9) NOT NULL,
`meetingID` int(9) NOT NULL,
`hasAccount` tinyint(1) NOT NULL DEFAULT '0',
`accepted` tinyint(1) NOT NULL,
`rejected` tinyint(1) NOT NULL,
`attended` tinyint(1) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `details`
--

CREATE TABLE `details` (
`batchLastRun` datetime NOT NULL,
`batchNextRun` datetime NOT NULL,
`batchIntervalHours` int(5) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


-- --------------------------------------------------------

--
-- Table structure for table `location`
--

CREATE TABLE `location` (
`locationID` int(9) NOT NULL,
`locationName` varchar(50) NOT NULL,
`address` varchar(60) NOT NULL,
`postcode` varchar(20) NOT NULL,
`openTime` time NOT NULL,
`closeTime` time NOT NULL,
`isOpenSpace` tinyint(1) NOT NULL DEFAULT '0',
`locationDescription` varchar(500) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


-- --------------------------------------------------------

--
-- Table structure for table `meeting`
--

CREATE TABLE `meeting` (
`meetingID` int(9) NOT NULL,
`accountID` int(9) NOT NULL,
`title` varchar(50) NOT NULL,
`numberOfPeople` int(4) NOT NULL,
`meetingTime` datetime NOT NULL,
`meetingDuration` time NOT NULL,
`locationID` int(9) NOT NULL,
`roomID` int(9) NOT NULL,
`booked` tinyint(1) NOT NULL DEFAULT '0',
`bookingFailed` tinyint(1) NOT NULL DEFAULT '0',
`meetingDescription` varchar(500) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


-- --------------------------------------------------------

--
-- Table structure for table `meetingpreferences`
--

CREATE TABLE `meetingpreferences` (
`meetingID` int(9) NOT NULL,
`meetingPriority` int(1) NOT NULL,
`locationID` int(9) NOT NULL,
`locationPriority` int(1) NOT NULL,
`roomID` int(9) NOT NULL,
`roomPriority` int(1) NOT NULL,
`futureLimit` date NOT NULL,
`AMPreferred` tinyint(1) NOT NULL,
`timePriority` int(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


-- --------------------------------------------------------

--
-- Table structure for table `noaccountattendee`
--

CREATE TABLE `noaccountattendee` (
`attendeeID` int(9) NOT NULL,
`attendeeName` varchar(50) NOT NULL,
`email` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `room`
--

CREATE TABLE `room` (
`roomID` int(9) NOT NULL,
`roomName` varchar(50) NOT NULL,
`locationID` int(9) NOT NULL,
`capacity` int(4) NOT NULL,
`roomDescription` varchar(100) NOT NULL,
`cost` decimal(7,2) NOT NULL,
`cleanupTime` time NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


--
-- Indexes for dumped tables
--

--
-- Indexes for table `account`
--
ALTER TABLE `account`
ADD PRIMARY KEY (`accountID`),
ADD UNIQUE KEY `email` (`email`);

--
-- Indexes for table `accountattendee`
--
ALTER TABLE `accountattendee`
ADD KEY `attendeeID` (`attendeeID`),
ADD KEY `userattendee_ibfk_2` (`accountID`);

--
-- Indexes for table `attendee`
--
ALTER TABLE `attendee`
ADD PRIMARY KEY (`attendeeID`),
ADD KEY `meetingID` (`meetingID`);

--
-- Indexes for table `location`
--
ALTER TABLE `location`
ADD PRIMARY KEY (`locationID`),
ADD UNIQUE KEY `locationName` (`locationName`,`postcode`);

--
-- Indexes for table `meeting`
--
ALTER TABLE `meeting`
ADD PRIMARY KEY (`meetingID`),
ADD KEY `userID` (`accountID`),
ADD KEY `locationID` (`locationID`),
ADD KEY `meeting_ibfk_3` (`roomID`);

--
-- Indexes for table `meetingpreferences`
--
ALTER TABLE `meetingpreferences`
ADD KEY `meetingID` (`meetingID`),
ADD KEY `locationID` (`locationID`),
ADD KEY `roomID` (`roomID`);

--
-- Indexes for table `noaccountattendee`
--
ALTER TABLE `noaccountattendee`
ADD KEY `attendeeID` (`attendeeID`);

--
-- Indexes for table `room`
--
ALTER TABLE `room`
ADD PRIMARY KEY (`roomID`),
ADD KEY `locationID` (`locationID`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `account`
--
ALTER TABLE `account`
MODIFY `accountID` int(9) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9308;

--
-- AUTO_INCREMENT for table `attendee`
--
ALTER TABLE `attendee`
MODIFY `attendeeID` int(9) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=233799;

--
-- AUTO_INCREMENT for table `location`
--
ALTER TABLE `location`
MODIFY `locationID` int(9) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=602;

--
-- AUTO_INCREMENT for table `meeting`
--
ALTER TABLE `meeting`
MODIFY `meetingID` int(9) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15623;

--
-- AUTO_INCREMENT for table `room`
--
ALTER TABLE `room`
MODIFY `roomID` int(9) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6002;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `accountattendee`
--
ALTER TABLE `accountattendee`
ADD CONSTRAINT `accountattendee_ibfk_1` FOREIGN KEY (`attendeeID`) REFERENCES `attendee` (`attendeeID`) ON DELETE CASCADE,
ADD CONSTRAINT `accountattendee_ibfk_2` FOREIGN KEY (`accountID`) REFERENCES `account` (`accountID`) ON DELETE CASCADE;

--
-- Constraints for table `attendee`
--
ALTER TABLE `attendee`
ADD CONSTRAINT `attendee_ibfk_1` FOREIGN KEY (`meetingID`) REFERENCES `meeting` (`meetingID`) ON DELETE CASCADE;

--
-- Constraints for table `meeting`
--
ALTER TABLE `meeting`
ADD CONSTRAINT `meeting_ibfk_1` FOREIGN KEY (`accountID`) REFERENCES `account` (`accountID`) ON DELETE CASCADE,
ADD CONSTRAINT `meeting_ibfk_2` FOREIGN KEY (`locationID`) REFERENCES `location` (`locationID`) ON DELETE CASCADE,
ADD CONSTRAINT `meeting_ibfk_3` FOREIGN KEY (`roomID`) REFERENCES `room` (`roomID`) ON DELETE CASCADE;

--
-- Constraints for table `meetingpreferences`
--
ALTER TABLE `meetingpreferences`
ADD CONSTRAINT `meetingpreferences_ibfk_1` FOREIGN KEY (`meetingID`) REFERENCES `meeting` (`meetingID`) ON DELETE CASCADE,
ADD CONSTRAINT `meetingpreferences_ibfk_2` FOREIGN KEY (`locationID`) REFERENCES `location` (`locationID`) ON DELETE CASCADE,
ADD CONSTRAINT `meetingpreferences_ibfk_3` FOREIGN KEY (`roomID`) REFERENCES `room` (`roomID`) ON DELETE CASCADE;

--
-- Constraints for table `noaccountattendee`
--
ALTER TABLE `noaccountattendee`
ADD CONSTRAINT `noaccountattendee_ibfk_1` FOREIGN KEY (`attendeeID`) REFERENCES `attendee` (`attendeeID`) ON DELETE CASCADE;

--
-- Constraints for table `room`
--
ALTER TABLE `room`
ADD CONSTRAINT `room_ibfk_1` FOREIGN KEY (`locationID`) REFERENCES `location` (`locationID`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
