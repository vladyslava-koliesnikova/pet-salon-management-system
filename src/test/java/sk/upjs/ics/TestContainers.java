package sk.upjs.ics;

import org.junit.jupiter.api.BeforeEach;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.MountableFile;

// Code taken from https://java.testcontainers.org/test_framework_integration/manual_lifecycle_control/
public abstract class TestContainers {

    static final MySQLContainer<?> mysql;

    static {
        mysql = new MySQLContainer<>("mysql:8.4.2")
                .withDatabaseName("petsalon")
                .withCopyFileToContainer(
                        MountableFile.forHostPath("./init.sql"), "/docker-entrypoint-initdb.d/init.sql"
                );
        mysql.start();
        System.setProperty("DB_JDBC", mysql.getJdbcUrl());
        System.setProperty("DB_USER", mysql.getUsername());
        System.setProperty("DB_PASSWORD", mysql.getPassword());
    }


    protected void clearTables() {
        var jdbc = Factory.INSTANCE.getMysqlJdbcOperations();
        jdbc.update("DELETE FROM orders_has_service_types");
        jdbc.update("DELETE FROM service_types");
        jdbc.update("DELETE FROM orders");
        jdbc.update("DELETE FROM dogs");
        jdbc.update("DELETE FROM discounts");
        jdbc.update("DELETE FROM users");
    }

    @BeforeEach
    void setUpBase() {
        clearTables();

        var jdbc = Factory.INSTANCE.getMysqlJdbcOperations();


        // users
        jdbc.update("INSERT INTO users (id, role, first_name, last_name, password, email, birth_date) VALUES (1, 'COSTUMER', 'John', 'Doe', 'password123', 'john.doe@example.com', '1990-05-12')");
        jdbc.update("INSERT INTO users (id, role, first_name, last_name, password, email, birth_date) VALUES (2, 'EMPLOYEE', 'Jane', 'Smith', 'securepassword', 'jane.smith@example.com', '1985-11-23')");
        jdbc.update("INSERT INTO users (id, role, first_name, last_name, password, email, birth_date) VALUES (3, 'COSTUMER', 'Michael', 'Johnson', 'qwerty123', 'michael.j@example.com', '1995-08-20')");
        jdbc.update("INSERT INTO users (id, role, first_name, last_name, password, email, birth_date) VALUES (4, 'COSTUMER', 'Emily', 'Davis', 'hello2023', 'emily.davis@example.com', '1988-03-15')");
        jdbc.update("INSERT INTO users (id, role, first_name, last_name, password, email, birth_date) VALUES (5, 'EMPLOYEE', 'Robert', 'Garcia', 'robertpass', 'robert.garcia@example.com', '1978-06-22');");

        //discounts
        jdbc.update("INSERT INTO discounts (id, name, discount_percent, start_date, end_date) VALUES (1, 'EASTER', 15, '2024-03-20', '2024-04-28')");
        jdbc.update("INSERT INTO discounts (id, name, discount_percent, start_date, end_date) VALUES(2, 'CHRISTMAS', 25, '2024-12-01', '2024-12-25')");
        jdbc.update("INSERT INTO discounts (id, name, discount_percent, start_date, end_date) VALUES(3, 'NEW_YEAR', 20, '2024-12-26', '2025-01-02')");

        //dogs
        jdbc.update("INSERT INTO dogs (id, name, sex, birth_date, users_id) VALUES (1, 'Buddy', 'M', '2010-05-12', 1)");
        jdbc.update("INSERT INTO dogs (id, name, sex, birth_date, users_id) VALUES (2, 'Bella', 'F', '2015-05-10', 2)");
        jdbc.update("INSERT INTO dogs (id, name, sex, birth_date, users_id) VALUES (3, 'Max', 'M', '2020-10-12', 3)");
        jdbc.update("INSERT INTO dogs (id, name, sex, birth_date, users_id) VALUES (4, 'Luna', 'F', '2020-06-15', 4)");
        jdbc.update("INSERT INTO dogs (id, name, sex, birth_date, users_id) VALUES (5, 'Charlie', 'M', '2018-07-20', 5)");

        //orders
        jdbc.update("INSERT INTO orders (id, date_time, discounts_id, dogs_id) VALUES (1, '2024-12-05 10:00:00', 1, 1)");
        jdbc.update("INSERT INTO orders (id, date_time, discounts_id, dogs_id) VALUES (2, '2024-06-14 12:00:00', 2, 5)");
        jdbc.update("INSERT INTO orders (id, date_time, discounts_id, dogs_id) VALUES (3, '2024-07-18 14:00:00', 3, 2)");
        jdbc.update("INSERT INTO orders (id, date_time, discounts_id, dogs_id) VALUES (4, '2024-10-10 16:00:00', NULL, 3)");

        //service_types
        jdbc.update("INSERT INTO service_types (id, name, price) VALUES (1, 'Grooming', 25.00)");
        jdbc.update("INSERT INTO service_types (id, name, price) VALUES (2, 'Bathing', 15.00)");
        jdbc.update("INSERT INTO service_types (id, name, price) VALUES (3, 'Nail Trimming', 10.00)");

        //service_types_id
        jdbc.update("INSERT INTO orders_has_service_types (orders_id, service_types_id) VALUES (1, 1)");
        jdbc.update("INSERT INTO orders_has_service_types (orders_id, service_types_id) VALUES (2, 2)");
        jdbc.update("INSERT INTO orders_has_service_types (orders_id, service_types_id) VALUES (3, 2)");
        jdbc.update("INSERT INTO orders_has_service_types (orders_id, service_types_id) VALUES (4, 3)");
    }
}