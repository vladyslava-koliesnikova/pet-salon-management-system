# Pet Salon Management System 🐾

A desktop application for managing a pet grooming salon, developed using JavaFX and MySQL.

The application supports customer registration, pet management, appointment booking, and administrative features for salon employees.

## Features

### Customer
- User registration and login
- Manage registered dogs
- Book grooming appointments
- Select available salon services

### Employee
- View all appointments, including upcoming and past bookings
- Manage and delete bookings
- View customer and employee information
- View salon statistics
- Export bookings to CSV

## Tech Stack

- **Java 21**
- **JavaFX 23** — desktop user interface
- **MySQL 8** — relational database
- **Spring JDBC** — database access
- **Maven** — dependency management and build
- **Docker Compose** — database setup
- **JUnit 5** — unit testing
- **Testcontainers** — MySQL integration testing

## Getting Started

### Prerequisites

- Java 21
- Maven
- Docker Desktop

### 1. Clone the repository

```bash
git clone <repository-url>
cd petSalon
```

### 2. Start the database

```bash
docker compose up -d
```

Docker Compose starts a MySQL database on port `3307`. The database schema and initial sample data are provided in `init.sql`.

### 3. Run the application

```bash
mvn javafx:run
```

## Testing

The project includes unit tests for in-memory DAO implementations and integration tests for MySQL DAO implementations.

Run the unit tests:

```bash
mvn "-Dtest=Memory*Test" test
```

Run the full test suite:

```bash
mvn test
```

Integration tests use Testcontainers and require a compatible, running Docker environment.

## Project Structure

```text
petSalon/
├── src/
│   ├── main/
│   │   ├── java/
│   │   └── resources/
│   └── test/
│       └── java/
├── docker-compose.yml
├── init.sql
├── pom.xml
└── README.md
```

## About the Project

Pet Salon was originally developed as a collaborative university project.

The application demonstrates object-oriented programming, relational database integration, the DAO pattern, desktop UI development, and automated testing.

Further improvements include CSV booking export and preparation for local deployment using Docker Compose.