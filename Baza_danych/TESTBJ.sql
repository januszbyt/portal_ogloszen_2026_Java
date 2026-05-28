-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: Maj 28, 2026 at 11:12 AM
-- Wersja serwera: 10.4.28-MariaDB
-- Wersja PHP: 8.2.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `TESTBJ`
--

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `Applications`
--

DROP TABLE IF EXISTS `Applications`;
CREATE TABLE `Applications` (
  `ApplicationID` int(11) NOT NULL,
  `OfferID` int(11) NOT NULL,
  `CandidateID` int(11) NOT NULL,
  `StatusID` int(11) NOT NULL,
  `AppliedAt` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `Applications`
--

INSERT INTO `Applications` (`ApplicationID`, `OfferID`, `CandidateID`, `StatusID`, `AppliedAt`) VALUES
(301, 201, 6, 3, '2026-05-28 11:00:16'),
(302, 201, 7, 1, '2026-05-28 11:00:16'),
(303, 203, 6, 4, '2026-05-15 12:00:00');

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `ApplicationStatuses`
--

DROP TABLE IF EXISTS `ApplicationStatuses`;
CREATE TABLE `ApplicationStatuses` (
  `StatusID` int(11) NOT NULL,
  `StatusName` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `ApplicationStatuses`
--

INSERT INTO `ApplicationStatuses` (`StatusID`, `StatusName`) VALUES
(1, 'Przesłano'),
(2, 'Oczekująca'),
(3, 'W toku'),
(4, 'Odrzucony');

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `Candidates`
--

DROP TABLE IF EXISTS `Candidates`;
CREATE TABLE `Candidates` (
  `CandidateID` int(11) NOT NULL,
  `FirstName` varchar(100) NOT NULL,
  `LastName` varchar(100) NOT NULL,
  `CVFilePath` varchar(100) DEFAULT NULL,
  `LinkedinURL` varchar(255) DEFAULT NULL,
  `GithubURL` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `Candidates`
--

INSERT INTO `Candidates` (`CandidateID`, `FirstName`, `LastName`, `CVFilePath`, `LinkedinURL`, `GithubURL`) VALUES
(6, 'Jan', 'Kowalski', 'jan_kowalski_cv.pdf', 'linkedin.com/in/jankowalski', 'github.com/jkowalski'),
(7, 'Anna', 'Nowak', 'anna_nowak_cv.pdf', NULL, NULL);

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `Categories`
--

DROP TABLE IF EXISTS `Categories`;
CREATE TABLE `Categories` (
  `CategoryID` int(11) NOT NULL,
  `CategoryName` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `Categories`
--

INSERT INTO `Categories` (`CategoryID`, `CategoryName`) VALUES
(2, 'Budownictwo'),
(3, 'Finanse'),
(1, 'IT / Software'),
(4, 'Sprzedaż');

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `Employers`
--

DROP TABLE IF EXISTS `Employers`;
CREATE TABLE `Employers` (
  `EmployerID` int(11) NOT NULL,
  `CompanyName` varchar(200) NOT NULL,
  `Description` text DEFAULT NULL,
  `NIP` varchar(10) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `Employers`
--

INSERT INTO `Employers` (`EmployerID`, `CompanyName`, `Description`, `NIP`) VALUES
(8, 'Tech-Pol Sp. z o.o.', 'Innowacyjna firma programistyczna z Warszawy.', '1234567890'),
(9, 'Bud-Max S.A.', 'Wiodąca firma budowlana w Polsce południowej.', '0987654321');

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `JobOffers`
--

DROP TABLE IF EXISTS `JobOffers`;
CREATE TABLE `JobOffers` (
  `OfferID` int(11) NOT NULL,
  `EmployerID` int(11) NOT NULL,
  `Title` varchar(200) NOT NULL,
  `CategoryID` int(11) NOT NULL,
  `Description` text NOT NULL,
  `SalaryMIN` decimal(10,2) DEFAULT NULL,
  `SalaryMAX` decimal(10,2) DEFAULT NULL,
  `Location` varchar(100) DEFAULT NULL,
  `OfferStatusID` int(11) NOT NULL,
  `CreatedAt` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `JobOffers`
--

INSERT INTO `JobOffers` (`OfferID`, `EmployerID`, `Title`, `CategoryID`, `Description`, `SalaryMIN`, `SalaryMAX`, `Location`, `OfferStatusID`, `CreatedAt`) VALUES
(201, 8, 'Senior Java Developer', 1, 'Szukamy programisty z 5-letnim doświadczeniem w Javie 21. Wymagany Spring Boot.', 15000.00, 22000.00, 'Warszawa / Zdalnie', 1, '2026-05-28 11:00:16'),
(202, 8, 'Junior Frontend Developer', 1, 'Dołącz do naszego zespołu UI/UX. Wymagany React i CSS.', 6000.00, 9000.00, 'Zdalnie', 2, '2026-05-01 10:00:00'),
(203, 9, 'Kierownik Budowy', 2, 'Poszukujemy kierownika z uprawnieniami do prowadzenia inwestycji deweloperskich.', 10000.00, 14000.00, 'Kraków', 1, '2026-05-28 11:00:16');

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `OfferStatuses`
--

DROP TABLE IF EXISTS `OfferStatuses`;
CREATE TABLE `OfferStatuses` (
  `OfferStatusID` int(11) NOT NULL,
  `StatusName` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `OfferStatuses`
--

INSERT INTO `OfferStatuses` (`OfferStatusID`, `StatusName`) VALUES
(1, 'Aktywna'),
(2, 'Nieaktywna');

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `Users`
--

DROP TABLE IF EXISTS `Users`;
CREATE TABLE `Users` (
  `UserID` int(11) NOT NULL,
  `Email` varchar(255) NOT NULL,
  `PasswordHash` varchar(255) NOT NULL,
  `Role` varchar(20) NOT NULL,
  `IsBlocked` tinyint(1) DEFAULT 0,
  `CreatedAt` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `Users`
--

INSERT INTO `Users` (`UserID`, `Email`, `PasswordHash`, `Role`, `IsBlocked`, `CreatedAt`) VALUES
(6, 'kandydat1@test.pl', '55028977893da087bfb1bfe650d8ad9e407e229590ce5c58bf61c78a9238ba69', 'Candidate', 0, '2026-05-28 10:39:58'),
(7, 'kandydat2@test.pl', '55028977893da087bfb1bfe650d8ad9e407e229590ce5c58bf61c78a9238ba69', 'Candidate', 0, '2026-05-28 10:40:58'),
(8, 'pracodawca1@test.pl', '55028977893da087bfb1bfe650d8ad9e407e229590ce5c58bf61c78a9238ba69', 'Employer', 0, '2026-05-28 10:42:17'),
(9, 'pracodawca2@test.pl', '55028977893da087bfb1bfe650d8ad9e407e229590ce5c58bf61c78a9238ba69', 'Employer', 0, '2026-05-28 10:42:41'),
(10, 'admin@test.pl', '55028977893da087bfb1bfe650d8ad9e407e229590ce5c58bf61c78a9238ba69', 'Admin', 0, '2026-05-28 10:48:55');

--
-- Indeksy dla zrzutów tabel
--

--
-- Indeksy dla tabeli `Applications`
--
ALTER TABLE `Applications`
  ADD PRIMARY KEY (`ApplicationID`),
  ADD UNIQUE KEY `OfferID` (`OfferID`,`CandidateID`),
  ADD KEY `CandidateID` (`CandidateID`),
  ADD KEY `StatusID` (`StatusID`);

--
-- Indeksy dla tabeli `ApplicationStatuses`
--
ALTER TABLE `ApplicationStatuses`
  ADD PRIMARY KEY (`StatusID`);

--
-- Indeksy dla tabeli `Candidates`
--
ALTER TABLE `Candidates`
  ADD PRIMARY KEY (`CandidateID`);

--
-- Indeksy dla tabeli `Categories`
--
ALTER TABLE `Categories`
  ADD PRIMARY KEY (`CategoryID`),
  ADD UNIQUE KEY `CategoryName` (`CategoryName`);

--
-- Indeksy dla tabeli `Employers`
--
ALTER TABLE `Employers`
  ADD PRIMARY KEY (`EmployerID`);

--
-- Indeksy dla tabeli `JobOffers`
--
ALTER TABLE `JobOffers`
  ADD PRIMARY KEY (`OfferID`),
  ADD KEY `EmployerID` (`EmployerID`),
  ADD KEY `CategoryID` (`CategoryID`),
  ADD KEY `OfferStatusID` (`OfferStatusID`);

--
-- Indeksy dla tabeli `OfferStatuses`
--
ALTER TABLE `OfferStatuses`
  ADD PRIMARY KEY (`OfferStatusID`);

--
-- Indeksy dla tabeli `Users`
--
ALTER TABLE `Users`
  ADD PRIMARY KEY (`UserID`),
  ADD UNIQUE KEY `Email` (`Email`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `Applications`
--
ALTER TABLE `Applications`
  MODIFY `ApplicationID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=304;

--
-- AUTO_INCREMENT for table `ApplicationStatuses`
--
ALTER TABLE `ApplicationStatuses`
  MODIFY `StatusID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `Categories`
--
ALTER TABLE `Categories`
  MODIFY `CategoryID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `JobOffers`
--
ALTER TABLE `JobOffers`
  MODIFY `OfferID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=204;

--
-- AUTO_INCREMENT for table `OfferStatuses`
--
ALTER TABLE `OfferStatuses`
  MODIFY `OfferStatusID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `Users`
--
ALTER TABLE `Users`
  MODIFY `UserID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `Applications`
--
ALTER TABLE `Applications`
  ADD CONSTRAINT `applications_ibfk_1` FOREIGN KEY (`OfferID`) REFERENCES `JobOffers` (`OfferID`) ON DELETE CASCADE,
  ADD CONSTRAINT `applications_ibfk_2` FOREIGN KEY (`CandidateID`) REFERENCES `Candidates` (`CandidateID`) ON DELETE CASCADE,
  ADD CONSTRAINT `applications_ibfk_3` FOREIGN KEY (`StatusID`) REFERENCES `ApplicationStatuses` (`StatusID`);

--
-- Constraints for table `Candidates`
--
ALTER TABLE `Candidates`
  ADD CONSTRAINT `candidates_ibfk_1` FOREIGN KEY (`CandidateID`) REFERENCES `Users` (`UserID`) ON DELETE CASCADE;

--
-- Constraints for table `Employers`
--
ALTER TABLE `Employers`
  ADD CONSTRAINT `employers_ibfk_1` FOREIGN KEY (`EmployerID`) REFERENCES `Users` (`UserID`) ON DELETE CASCADE;

--
-- Constraints for table `JobOffers`
--
ALTER TABLE `JobOffers`
  ADD CONSTRAINT `joboffers_ibfk_1` FOREIGN KEY (`EmployerID`) REFERENCES `Employers` (`EmployerID`) ON DELETE CASCADE,
  ADD CONSTRAINT `joboffers_ibfk_2` FOREIGN KEY (`CategoryID`) REFERENCES `Categories` (`CategoryID`),
  ADD CONSTRAINT `joboffers_ibfk_3` FOREIGN KEY (`OfferStatusID`) REFERENCES `OfferStatuses` (`OfferStatusID`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
