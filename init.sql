SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema petsalon
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `petsalon` DEFAULT CHARACTER SET utf8 ;
USE `petsalon` ;

-- -----------------------------------------------------
-- Table `petsalon`.`users`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `petsalon`.`users` ;

CREATE TABLE IF NOT EXISTS `petsalon`.`users` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `role` ENUM('EMPLOYEE', 'COSTUMER') NOT NULL,
    `first_name` VARCHAR(45) NOT NULL,
    `last_name` VARCHAR(45) NOT NULL,
    `password` VARCHAR(60) NOT NULL,
    `email` VARCHAR(45) NOT NULL,
    `birth_date` DATE NOT NULL,
    PRIMARY KEY (`id`))
    ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `petsalon`.`dogs`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `petsalon`.`dogs` ;

CREATE TABLE IF NOT EXISTS `petsalon`.`dogs` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(45) NOT NULL,
    `sex` ENUM('M', 'F', 'UNKNOWN') NOT NULL,
    `birth_date` DATE NOT NULL,
    `users_id` BIGINT NOT NULL,
    PRIMARY KEY (`id`),
    INDEX `fk_dogs_users_idx` (`users_id` ASC) VISIBLE,
    CONSTRAINT `fk_dogs_users`
        FOREIGN KEY (`users_id`)
            REFERENCES `petsalon`.`users` (`id`)
            ON DELETE CASCADE
            ON UPDATE CASCADE)
    ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `petsalon`.`discounts`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `petsalon`.`discounts` ;

CREATE TABLE IF NOT EXISTS `petsalon`.`discounts` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `name` ENUM('EASTER', 'NEW_YEAR', 'CHRISTMAS') NOT NULL,
    `discount_percent` DECIMAL NOT NULL,
    `start_date` DATE NOT NULL,
    `end_date` DATE NOT NULL,
    PRIMARY KEY (`id`))
    ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `petsalon`.`orders`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `petsalon`.`orders` ;

CREATE TABLE IF NOT EXISTS `petsalon`.`orders` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `discounts_id` BIGINT NULL,
    `date_time` DATETIME NOT NULL,
    `dogs_id` BIGINT NULL,
    PRIMARY KEY (`id`),
    INDEX `fk_orders_discounts1_idx` (`discounts_id` ASC) VISIBLE,
    INDEX `fk_orders_dogs1_idx` (`dogs_id` ASC) VISIBLE,
    CONSTRAINT `fk_orders_discounts`
        FOREIGN KEY (`discounts_id`)
            REFERENCES `petsalon`.`discounts` (`id`)
            ON DELETE SET NULL
            ON UPDATE CASCADE,
    CONSTRAINT `fk_orders_dogs1`
        FOREIGN KEY (`dogs_id`)
            REFERENCES `petsalon`.`dogs` (`id`)
            ON DELETE CASCADE
            ON UPDATE CASCADE)
    ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `petsalon`.`service_types`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `petsalon`.`service_types` ;

CREATE TABLE IF NOT EXISTS `petsalon`.`service_types` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(45) NOT NULL,
    `price` DECIMAL NOT NULL,
    PRIMARY KEY (`id`))
    ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `petsalon`.`orders_has_service_types`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `petsalon`.`orders_has_service_types` ;

CREATE TABLE IF NOT EXISTS `petsalon`.`orders_has_service_types` (
    `orders_id` BIGINT NOT NULL,
    `service_types_id` BIGINT NOT NULL,
    PRIMARY KEY (`orders_id`, `service_types_id`),
    INDEX `fk_orders_has_service_types_service_types1_idx` (`service_types_id` ASC) VISIBLE,
    INDEX `fk_orders_has_service_types_orders1_idx` (`orders_id` ASC) VISIBLE,
    CONSTRAINT `fk_orders_has_service_types_orders`
        FOREIGN KEY (`orders_id`)
            REFERENCES `petsalon`.`orders` (`id`)
                ON DELETE NO ACTION
                ON UPDATE NO ACTION,
    CONSTRAINT `fk_orders_has_service_types_service_types`
        FOREIGN KEY (`service_types_id`)
            REFERENCES `petsalon`.`service_types` (`id`)
                ON DELETE NO ACTION
                ON UPDATE NO ACTION)
    ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

INSERT INTO users (id, role, first_name, last_name, password, email, birth_date)
VALUES (1, 'COSTUMER', 'John', 'Doe', '$2a$10$mToYXRsuV/o431kcIROwIOklHU5cAoVFjDaRlpSpgyQzbP5LliAe2', 'john.doe@example.com', '1990-05-12'),
       (2, 'EMPLOYEE', 'Jane', 'Smith', '$2a$10$j7r2JAFxzxv9/O1DYGvUqe5rs4NFDdmPO02CyqKuj7PmrthrJgnsm', 'jane.smith@example.com', '1985-11-23'),
       (3, 'COSTUMER', 'Michael', 'Johnson', '$2a$10$AgNBHZJF0QK9dmDrkNo2W.2.5fFeSnD551pw7ejs0eurWnfsc7Z.2', 'michael.j@example.com', '1995-08-20'),
       (4, 'COSTUMER', 'Emily', 'Davis', '$2a$10$xQfvkzdxVsgGyJ7dsI7ua.dlOXIMN4B/3wTWtWKROky8wJSe/vv7y', 'emily.davis@example.com', '1988-03-15'),
       (5, 'EMPLOYEE', 'Robert', 'Garcia', '$2a$10$EiLhFgqm1t9T5ETtSlGs2uea3SfokVAikSILLmBXtuxvTCUjx1Z26', 'robert.garcia@example.com', '1978-06-22'),
       (6, 'COSTUMER', 'Sarah', 'Miller', '$2a$10$7AfljxdiAcQtGaGaGEodGOmWE0Sza9CNPzTDibPkC5loykMKrFJJ2', 'sarah.miller@example.com', '2000-07-30'),
       (7, 'EMPLOYEE', 'William', 'Martinez', '$2a$10$SveQ0ApmoiPiVkdyvuzPqeIW1egky2O73WOQIPHmWEPMGmyV1nV/e', 'william.martinez@example.com', '1992-09-10'),
       (8, 'COSTUMER', 'Olivia', 'Hernandez', '$2a$10$YCP1WgP2/5IJ6S6s/y04BeT21sRsnTIKbIcMNeLuf7zHYXpfqJtKm', 'olivia.h@example.com', '1998-01-25'),
       (9, 'EMPLOYEE', 'James', 'Lopez', '$2a$10$aDi12SBwO870l9T6zqxFNOxiaqksz5msZWU6XNMJmKR2yRvXqnv2C', 'james.lopez@example.com', '1980-04-17'),
       (10, 'COSTUMER', 'Charlotte', 'Wright', '$2a$10$HlLmOR8JmThE8pRbNnHglevXA2QrH3Phrg27vROOUNMvRpQZ.6XFu', 'charlotte.w@example.com', '2002-05-11');

INSERT INTO discounts (id, name, discount_percent, start_date, end_date)
VALUES (1, 'EASTER', 15, '2024-03-20', '2024-04-28'),
       (2, 'CHRISTMAS', 25, '2024-12-01', '2024-12-25'),
       (3, 'NEW_YEAR', 20, '2024-12-26', '2025-01-02');

INSERT INTO dogs (id, name, sex, birth_date, users_id)
VALUES (1, 'Buddy', 'M', '2010-05-12', 1),
       (2, 'Bella', 'F', '2015-05-10', 1),
       (3, 'Max', 'M', '2020-10-12', 3),
       (4, 'Luna', 'F', '2020-06-15', 3),
       (5, 'Charlie', 'M', '2018-07-20', 5),
       (6, 'Daisy', 'F', '2021-11-04', 6),
       (7, 'Rocky', 'M', '2023-08-01', 7),
       (8, 'Molly', 'F', '2017-01-21', 8),
       (9, 'Bailey', 'F', '2014-04-04', 9),
       (10, 'Cooper', 'M', '2021-03-06', 10);
INSERT INTO orders (id, date_time, discounts_id, dogs_id)
VALUES (1, '2024-12-05 10:00:00', 1, 3),
       (2, '2024-06-14 12:00:00', 2, 7),
       (3, '2024-07-18 14:00:00', NULL, 2),
       (4, '2024-12-16 12:00:00', NULL, 3),
       (5, '2024-12-20 16:00:00', NULL, 1), -- Assuming dog ID 1 exists
       (6, '2024-12-25 12:00:00', NULL, 3);



INSERT INTO service_types (id, name, price)
VALUES (1, 'Grooming', 25.00),
       (2, 'Bathing', 15.00),
       (3, 'Nail Trimming', 10.00);

INSERT INTO orders_has_service_types (orders_id, service_types_id)
VALUES (1, 1),
       (1, 2),
       (1, 3),
       (2, 2),
       (3, 2),
       (4, 1),
       (5, 2),
       (6, 3);



