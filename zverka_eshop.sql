-- phpMyAdmin SQL Dump
-- version 4.7.4
-- https://www.phpmyadmin.net/
--
-- Hostiteľ: 127.0.0.1
-- Čas generovania: So 12.Jan 2019, 13:29
-- Verzia serveru: 10.1.28-MariaDB
-- Verzia PHP: 7.1.11

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Databáza: `zverka_eshop`
--

-- --------------------------------------------------------

--
-- Štruktúra tabuľky pre tabuľku `kosik`
--

CREATE TABLE `kosik` (
  `ID` int(11) NOT NULL,
  `ID_pouzivatela` int(11) NOT NULL,
  `ID_tovaru` int(11) NOT NULL,
  `cena` int(11) NOT NULL,
  `ks` int(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Sťahujem dáta pre tabuľku `kosik`
--

INSERT INTO `kosik` (`ID`, `ID_pouzivatela`, `ID_tovaru`, `cena`, `ks`) VALUES
(14, 1, 11, 24, 1),
(12, 1, 6, 74, 4),
(11, 1, 7, 22, 5),
(15, 1, 10, 30, 1);

-- --------------------------------------------------------

--
-- Štruktúra tabuľky pre tabuľku `obj_polozky`
--

CREATE TABLE `obj_polozky` (
  `ID` int(11) NOT NULL,
  `ID_objednavky` int(11) NOT NULL,
  `ID_tovaru` int(11) NOT NULL,
  `cena` int(11) NOT NULL,
  `ks` int(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Sťahujem dáta pre tabuľku `obj_polozky`
--

INSERT INTO `obj_polozky` (`ID`, `ID_objednavky`, `ID_tovaru`, `cena`, `ks`) VALUES
(1, 2, 5, 26, 2),
(2, 2, 7, 22, 1),
(3, 2, 9, 95, 2),
(4, 3, 5, 26, 1),
(6, 5, 8, 59, 1),
(7, 6, 5, 26, 1),
(8, 6, 6, 74, 1);

-- --------------------------------------------------------

--
-- Štruktúra tabuľky pre tabuľku `obj_zoznam`
--

CREATE TABLE `obj_zoznam` (
  `ID` int(11) NOT NULL,
  `obj_cislo` varchar(20) NOT NULL,
  `datum_objednavky` date NOT NULL,
  `ID_pouzivatela` int(11) NOT NULL,
  `suma` int(11) NOT NULL,
  `stav` varchar(20) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Sťahujem dáta pre tabuľku `obj_zoznam`
--

INSERT INTO `obj_zoznam` (`ID`, `obj_cislo`, `datum_objednavky`, `ID_pouzivatela`, `suma`, `stav`) VALUES
(2, '20181216122416169', '2018-12-16', 1, 211, 'zaplatene'),
(3, '20181216131238042', '2018-12-16', 2, 25, 'objednane'),
(5, '20181216131652420', '2018-12-16', 2, 57, 'odoslane'),
(6, '20181216131804469', '2018-12-16', 1, 80, 'objednane');

-- --------------------------------------------------------

--
-- Štruktúra tabuľky pre tabuľku `pouzivatelia`
--

CREATE TABLE `pouzivatelia` (
  `ID` int(11) NOT NULL,
  `login` varchar(20) CHARACTER SET utf8 COLLATE utf8_slovak_ci NOT NULL,
  `heslo` varchar(20) CHARACTER SET utf8 COLLATE utf8_slovak_ci NOT NULL,
  `mail` varchar(40) CHARACTER SET utf8 COLLATE utf8_slovak_ci NOT NULL,
  `adresa` varchar(50) CHARACTER SET utf8 COLLATE utf8_slovak_ci NOT NULL,
  `zlava` int(11) NOT NULL,
  `meno` varchar(20) CHARACTER SET utf8 COLLATE utf8_slovak_ci NOT NULL,
  `priezvisko` varchar(20) CHARACTER SET utf8 COLLATE utf8_slovak_ci NOT NULL,
  `poznamky` text CHARACTER SET utf8 COLLATE utf8_slovak_ci,
  `prava` varchar(5) CHARACTER SET utf8 COLLATE utf8_slovak_ci NOT NULL DEFAULT 'user'
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Sťahujem dáta pre tabuľku `pouzivatelia`
--

INSERT INTO `pouzivatelia` (`ID`, `login`, `heslo`, `mail`, `adresa`, `zlava`, `meno`, `priezvisko`, `poznamky`, `prava`) VALUES
(1, 'jskalka', '123', 'nema@ukf.sk', 'Zeleninova 4, Nitra', 20, 'Jan ', 'Skalka', 'tester', 'user'),
(2, 'jmrkva', '123', 'jozko@mrkvicka.sk', 'Zahrada 11', 3, 'Jozef', 'Mrkva', 'druhý tester', 'user'),
(3, 'admin', 'supertajneheslo', 'admin@admin.admin', 'Adminova 12', 50, 'Admin', 'Admin', 'admnin', 'admin'),
(4, 'a', 'a', 'a@a.com', 'a', 10, 'a', 'a', '', 'user'),
(5, 'b', 'b', 'b@b.com', 'b', 3, 'b', 'b', NULL, 'user'),
(6, 'zverka', '123456', 'adam.zverka@student.ukf.sk', 'DatabÃ¡zovÃ¡ 4', 36, 'Adam', 'Zverka', NULL, 'admin');

-- --------------------------------------------------------

--
-- Štruktúra tabuľky pre tabuľku `sklad`
--

CREATE TABLE `sklad` (
  `ID` int(11) NOT NULL,
  `nazov` varchar(45) CHARACTER SET utf8 COLLATE utf8_slovak_ci NOT NULL,
  `mierka` int(11) NOT NULL,
  `vyrobca` varchar(15) CHARACTER SET utf8 COLLATE utf8_slovak_ci NOT NULL,
  `ks` int(11) NOT NULL,
  `cena` int(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Sťahujem dáta pre tabuľku `sklad`
--

INSERT INTO `sklad` (`ID`, `nazov`, `mierka`, `vyrobca`, `ks`, `cena`) VALUES
(5, 'Boeing 747 SCA & Space Shuttle', 144, 'Revell', 2, 26),
(6, 'Antonov An-225 Mrija', 144, 'Revell', 1, 74),
(7, 'Ford GT - Le Mans', 24, 'Revell', 1, 22),
(8, 'F/A-18E Super Hornet', 32, 'Revell', 8, 59),
(9, 'FIAT Abarth 695 SS/ Assetto Corsa', 12, 'Italeri', 3, 95),
(10, 'ADVAN Porsche 962C', 24, 'Hasegawa', 6, 30),
(11, 'Alfa Romeo Giulietta Spider 1300', 24, 'Italeri', 8, 24),
(12, 'Saturn V Apollo', 144, 'Revell', 3, 26),
(13, 'Boeing 707', 144, 'Airfix', 4, 19);

--
-- Kľúče pre exportované tabuľky
--

--
-- Indexy pre tabuľku `kosik`
--
ALTER TABLE `kosik`
  ADD PRIMARY KEY (`ID`);

--
-- Indexy pre tabuľku `obj_polozky`
--
ALTER TABLE `obj_polozky`
  ADD PRIMARY KEY (`ID`);

--
-- Indexy pre tabuľku `obj_zoznam`
--
ALTER TABLE `obj_zoznam`
  ADD PRIMARY KEY (`ID`);

--
-- Indexy pre tabuľku `pouzivatelia`
--
ALTER TABLE `pouzivatelia`
  ADD PRIMARY KEY (`ID`);

--
-- Indexy pre tabuľku `sklad`
--
ALTER TABLE `sklad`
  ADD PRIMARY KEY (`ID`);

--
-- AUTO_INCREMENT pre exportované tabuľky
--

--
-- AUTO_INCREMENT pre tabuľku `kosik`
--
ALTER TABLE `kosik`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT pre tabuľku `obj_polozky`
--
ALTER TABLE `obj_polozky`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT pre tabuľku `obj_zoznam`
--
ALTER TABLE `obj_zoznam`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT pre tabuľku `pouzivatelia`
--
ALTER TABLE `pouzivatelia`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT pre tabuľku `sklad`
--
ALTER TABLE `sklad`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=14;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
