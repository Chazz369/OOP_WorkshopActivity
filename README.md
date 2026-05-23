# JavaFX Inventory Management System

A desktop Inventory Management System built with **JavaFX 17** and **PostgreSQL** (via Supabase). The application uses **Maven** for dependency management.

## Prerequisites

Before running the project, ensure you have the following installed:
- **Java Development Kit (JDK) 17** or higher
- **Apache Maven** (for building and running the project)
- **Supabase** (or any PostgreSQL database)

## Core Dependencies
- **JavaFX 17.0.15**: For the Graphical User Interface.
- **PostgreSQL JDBC 42.7.3**: For connecting to the PostgreSQL/Supabase database.
- **jBCrypt 0.4**: For hashing and securely storing user passwords.
- **dotenv-java 3.0.0**: For securely loading environment variables (like database credentials).

## Setup & Installation

### 1. Database Setup
1. Create a new PostgreSQL database (e.g., using Supabase).
2. Open the `schema.sql` file located in the root of this repository.
3. Run the SQL commands in your database's SQL editor to create the necessary tables (`users`, `categories`, `items`, `sales_log`) and initial seed data.

### 2. Environment Variables (.env)
For security, database credentials are not hardcoded into the project. You must create a `.env` file in the root directory of the project (at the same level as `pom.xml`).

Create a file named `.env` and add the following keys with your own database credentials:
```env
# JDBC Connection URL (Make sure it includes your specific database URL)
DB_URL=jdbc:postgresql://your-database-host:5432/postgres

# Database Username
DB_USER=your_db_username

# Database Password
DB_PASSWORD=your_db_password
```
*(Note: The `.env` file is excluded from version control via `.gitignore` to keep your credentials safe.)*

### 3. Build the Project
Open your terminal or command prompt, navigate to the root directory of the project, and run:
```bash
mvn clean install
```
This command will download all the required dependencies specified in `pom.xml` and compile the code.

### 4. Run the Application
You can run the JavaFX application using the Maven JavaFX plugin:
```bash
mvn javafx:run
```
