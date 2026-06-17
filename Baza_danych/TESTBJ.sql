-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: Cze 17, 2026 at 10:21 PM
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
(303, 203, 6, 4, '2026-05-15 12:00:00'),
(304, 204, 16, 3, '2026-05-28 12:10:00'),
(305, 206, 16, 1, '2026-05-28 12:40:00'),
(306, 220, 17, 2, '2026-05-28 12:20:00'),
(307, 221, 17, 3, '2026-05-28 12:50:00'),
(308, 204, 18, 1, '2026-05-28 12:35:00'),
(309, 211, 18, 3, '2026-05-28 13:00:00'),
(310, 229, 19, 1, '2026-05-28 13:05:00'),
(311, 233, 19, 4, '2026-05-28 13:10:00'),
(312, 214, 20, 3, '2026-05-28 13:20:00'),
(313, 216, 20, 1, '2026-05-28 13:30:00'),
(314, 210, 21, 1, '2026-05-28 13:40:00'),
(315, 208, 21, 2, '2026-05-28 13:50:00');

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
(16, 'Michał', 'Wiśniewski', 'michal_wisniewski_cv.pdf', 'linkedin.com/in/mwisniewski', 'github.com/mwisniewski99'),
(17, 'Katarzyna', 'Wójcik', 'katarzyna_wojcik_cv.pdf', 'linkedin.com/in/kwojcik', NULL),
(18, 'Piotr', 'Zieliński', 'piotr_zielinski_cv.pdf', 'linkedin.com/in/pzielinski', 'github.com/pzielinski-dev'),
(19, 'Magdalena', 'Mazur', 'magda_mazur_cv.pdf', NULL, NULL),
(20, 'Tomasz', 'Krawczyk', 'tomasz_krawczyk_cv.pdf', 'linkedin.com/in/tkrawczyk', NULL),
(21, 'Aleksandra', 'Zając', 'aleksandra_zajac_cv.pdf', 'linkedin.com/in/azajac', 'github.com/azajac-ux');

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
(9, 'Bud-Max S.A.', 'Wiodąca firma budowlana w Polsce południowej.', '0987654321'),
(11, 'FinCorp S.A.', 'Międzynarodowa instytucja finansowa świadcząca usługi doradcze dla biznesu.', '1112223334'),
(12, 'Bud-Ekspert', 'Lider rynku nieruchomości komercyjnych. Zbudowaliśmy 50 biurowców w Polsce.', '2223334445'),
(13, 'Sprzedaż-Pro', 'Nowoczesna agencja outsourcingu sił sprzedaży B2B.', '3334445556'),
(14, 'Tech-Innovators', 'Software house tworzący rozwiązania oparte na sztucznej inteligencji dla medycyny.', '4445556667'),
(15, 'Retail-Giant', 'Największa sieć supermarketów w Europie Środkowej, stale rozwijająca swój zespół.', '5556667778');

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
(203, 9, 'Kierownik Budowy', 2, 'Poszukujemy kierownika z uprawnieniami do prowadzenia inwestycji deweloperskich.', 10000.00, 14000.00, 'Kraków', 1, '2026-05-28 11:00:16'),
(204, 14, 'Data Scientist (AI/ML)', 1, 'Szukamy eksperta od NLP i tworzenia modeli predykcyjnych w PyTorch/TensorFlow.', 18000.00, 26000.00, 'Zdalnie', 1, '2026-05-21 08:30:00'),
(205, 14, 'DevOps Engineer', 1, 'Wymagane minimum 3 lata komercyjnego doświadczenia z AWS, Kubernetes i CI/CD.', 16000.00, 24000.00, 'Wrocław', 1, '2026-05-22 09:15:00'),
(206, 8, 'Mid Python Developer', 1, 'Rozwój aplikacji backendowych w oparciu o framework Django i architekturę mikroserwisów.', 10000.00, 16000.00, 'Warszawa / Zdalnie', 1, '2026-05-23 10:00:00'),
(207, 14, 'Scrum Master', 1, 'Prowadzenie i facylitacja pracy dwóch zespołów deweloperskich (12 osób).', 12000.00, 16000.00, 'Zdalnie', 1, '2026-05-23 11:45:00'),
(208, 8, 'Junior QA Tester', 1, 'Wykonywanie testów manualnych, podstawy automatyzacji w Selenium (Java).', 5000.00, 7500.00, 'Kraków', 1, '2026-05-24 12:20:00'),
(209, 14, 'Senior Frontend (Angular)', 1, 'Utrzymanie starszych projektów i migracja dużej aplikacji z AngularJS do Angular 17.', 17000.00, 23000.00, 'Gdańsk', 2, '2026-05-05 13:00:00'),
(210, 8, 'UI/UX Designer', 1, 'Projektowanie użytecznych interfejsów w Figmie dla platformy e-commerce.', 8000.00, 13000.00, 'Poznań', 1, '2026-05-25 14:10:00'),
(211, 14, 'Cloud Architect (Azure)', 1, 'Projektowanie infrastruktury chmurowej od podstaw dla nowego klienta z USA.', 25000.00, 35000.00, 'Zdalnie', 1, '2026-05-26 15:30:00'),
(212, 8, 'Cybersecurity Specialist', 1, 'Wykonywanie testów penetracyjnych i dbanie o bezpieczeństwo infrastruktury firmy.', 14000.00, 21000.00, 'Warszawa', 1, '2026-05-27 16:00:00'),
(213, 14, 'Game Developer (Unity)', 1, 'Dołącz do zespołu tworzącego mobilne gry 3D. Wymagana świetna znajomość C#.', 9000.00, 15000.00, 'Wrocław', 2, '2026-04-10 10:00:00'),
(214, 9, 'Inżynier Budowy', 2, 'Wsparcie kierownika w nadzorze nad realizacją obiektów kubaturowych (biurowce).', 7000.00, 10000.00, 'Katowice', 1, '2026-05-20 08:00:00'),
(215, 12, 'Kosztorysant', 2, 'Przygotowywanie precyzyjnych przedmiarów i wycen robót budowlanych na przetargi.', 8000.00, 12000.00, 'Warszawa', 1, '2026-05-21 09:30:00'),
(216, 12, 'Architekt z uprawnieniami', 2, 'Samodzielne projektowanie nowoczesnych osiedli mieszkaniowych.', 9000.00, 14000.00, 'Kraków', 1, '2026-05-22 11:00:00'),
(217, 9, 'Operator Koparki', 2, 'Praca przy robotach ziemnych. Wymagane uprawnienia klasy I.', 6000.00, 8000.00, 'Rzeszów', 1, '2026-05-23 07:00:00'),
(218, 12, 'Inspektor Nadzoru', 2, 'Stała kontrola jakości wykonywanych prac żelbetowych na dużym obiekcie.', 11000.00, 15000.00, 'Gdańsk', 1, '2026-05-24 14:00:00'),
(219, 9, 'Geodeta', 2, 'Tyczenie obiektów w terenie i dokładna inwentaryzacja powykonawcza.', 6500.00, 9000.00, 'Terenowy / Kraków', 2, '2026-04-15 08:00:00'),
(220, 11, 'Główny Księgowy', 3, 'Prowadzenie pełnej księgowości spółki akcyjnej, raportowanie do NBP i GUS.', 15000.00, 20000.00, 'Warszawa', 1, '2026-05-15 09:00:00'),
(221, 11, 'Analityk Finansowy', 3, 'Tworzenie cyklicznych raportów zarządczych i prognoz finansowych (PowerBI, Excel).', 9000.00, 13000.00, 'Poznań', 1, '2026-05-18 10:30:00'),
(222, 15, 'Specjalista ds. Kadr i Płac', 3, 'Bieżąca obsługa 300+ pracowników, naliczanie wynagrodzeń w programie Optima.', 6500.00, 8500.00, 'Łódź', 1, '2026-05-20 12:00:00'),
(223, 11, 'Doradca Klienta VIP', 3, 'Obsługa klientów zamożnych w zakresie inwestycji i produktów strukturyzowanych.', 8000.00, 15000.00, 'Kraków', 1, '2026-05-22 13:00:00'),
(224, 15, 'Audytor Wewnętrzny', 3, 'Weryfikacja i optymalizacja procesów finansowych w całej naszej sieci sklepów.', 10000.00, 14000.00, 'Zdalnie', 1, '2026-05-25 15:00:00'),
(225, 11, 'Młodszy Księgowy', 3, 'Wprowadzanie faktur kosztowych, weryfikacja dokumentów, przygotowywanie przelewów.', 5000.00, 6500.00, 'Warszawa', 2, '2026-03-20 09:00:00'),
(226, 11, 'Dyrektor Finansowy (CFO)', 3, 'Zarządzanie strukturą kapitałową i budżetem całej grupy, bliska współpraca z CEO.', 30000.00, 45000.00, 'Warszawa', 1, '2026-05-26 10:00:00'),
(227, 13, 'Key Account Manager', 4, 'Bieżąca opieka nad kluczowymi klientami B2B w regionie Europy Środkowej.', 12000.00, 20000.00, 'Warszawa / Zdalnie', 1, '2026-05-18 10:00:00'),
(228, 13, 'Przedstawiciel Handlowy', 4, 'Aktywne pozyskiwanie klientów w terenie i budowanie relacji biznesowych.', 6000.00, 12000.00, 'Katowice', 1, '2026-05-19 11:15:00'),
(229, 15, 'Kierownik Sklepu', 4, 'Zarządzanie kilkunastoosobowym zespołem w jednym z naszych największych marketów.', 8000.00, 11000.00, 'Lublin', 1, '2026-05-21 12:30:00'),
(230, 15, 'Zastępca Kierownika Sklepu', 4, 'Wsparcie kierownika w układaniu grafików, zamawianiu towaru i obsłudze klienta.', 6000.00, 8000.00, 'Szczecin', 1, '2026-05-23 13:45:00'),
(231, 13, 'Telemarketer B2B', 4, 'Wykonywanie zimnych telefonów i umawianie spotkań handlowych dla działu sprzedaży.', 4500.00, 7000.00, 'Zdalnie', 1, '2026-05-25 14:00:00'),
(232, 13, 'Dyrektor Sprzedaży', 4, 'Opracowanie i egzekucja wieloletniej strategii sprzedażowej dla rynku polskiego.', 20000.00, 30000.00, 'Warszawa', 1, '2026-05-26 15:30:00'),
(233, 15, 'Kasjer / Sprzedawca', 4, 'Miła obsługa klienta przy kasie fiskalnej i dbanie o ekspozycję towaru na dziale.', 4500.00, 5500.00, 'Kraków', 1, '2026-05-28 08:00:00');

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
(8, 'pracodawca1@test.pl', '55028977893da087bfb1bfe650d8ad9e407e229590ce5c58bf61c78a9238ba69', 'Employer', 0, '2026-05-28 10:42:17'),
(9, 'pracodawca2@test.pl', '55028977893da087bfb1bfe650d8ad9e407e229590ce5c58bf61c78a9238ba69', 'Employer', 0, '2026-05-28 10:42:41'),
(10, 'admin@test.pl', '55028977893da087bfb1bfe650d8ad9e407e229590ce5c58bf61c78a9238ba69', 'Admin', 0, '2026-05-28 10:48:55'),
(11, 'hr@fincorp.pl', '55028977893da087bfb1bfe650d8ad9e407e229590ce5c58bf61c78a9238ba69', 'Employer', 0, '2026-05-10 09:00:00'),
(12, 'rekrutacja@bud-ekspert.pl', '55028977893da087bfb1bfe650d8ad9e407e229590ce5c58bf61c78a9238ba69', 'Employer', 0, '2026-05-12 10:15:00'),
(13, 'kontakt@sprzedaz-pro.pl', '55028977893da087bfb1bfe650d8ad9e407e229590ce5c58bf61c78a9238ba69', 'Employer', 0, '2026-05-15 11:30:00'),
(14, 'jobs@tech-innovators.com', '55028977893da087bfb1bfe650d8ad9e407e229590ce5c58bf61c78a9238ba69', 'Employer', 0, '2026-05-20 12:45:00'),
(15, 'kariera@retail-giant.pl', '55028977893da087bfb1bfe650d8ad9e407e229590ce5c58bf61c78a9238ba69', 'Employer', 0, '2026-05-25 14:00:00'),
(16, 'm.wisniewski@test.pl', '55028977893da087bfb1bfe650d8ad9e407e229590ce5c58bf61c78a9238ba69', 'Candidate', 0, '2026-05-28 12:00:00'),
(17, 'katarzyna.wojcik@test.pl', '55028977893da087bfb1bfe650d8ad9e407e229590ce5c58bf61c78a9238ba69', 'Candidate', 0, '2026-05-28 12:15:00'),
(18, 'piotr.zielinski@test.pl', '55028977893da087bfb1bfe650d8ad9e407e229590ce5c58bf61c78a9238ba69', 'Candidate', 0, '2026-05-28 12:30:00'),
(19, 'magda.mazur@test.pl', '55028977893da087bfb1bfe650d8ad9e407e229590ce5c58bf61c78a9238ba69', 'Candidate', 0, '2026-05-28 12:45:00'),
(20, 'tomasz.krawczyk@test.pl', '55028977893da087bfb1bfe650d8ad9e407e229590ce5c58bf61c78a9238ba69', 'Candidate', 0, '2026-05-28 13:00:00'),
(21, 'aleksandra.zajac@test.pl', '55028977893da087bfb1bfe650d8ad9e407e229590ce5c58bf61c78a9238ba69', 'Candidate', 0, '2026-05-28 13:15:00');

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
  MODIFY `ApplicationID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=316;

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
  MODIFY `OfferID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=234;

--
-- AUTO_INCREMENT for table `OfferStatuses`
--
ALTER TABLE `OfferStatuses`
  MODIFY `OfferStatusID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `Users`
--
ALTER TABLE `Users`
  MODIFY `UserID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=34;

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
