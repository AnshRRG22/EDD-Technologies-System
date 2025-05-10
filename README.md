# EDD Technologies System

EDD Technologies System is a full-featured, role-based desktop repair management application developed using **Java Swing** and **MySQL**, tailored for electronics service centers. The system allows **Admins**, **Technicians**, and **Customers** to manage repair jobs, track equipment, receive notifications, and perform role-specific operations in a structured and secure environment.

---

## 💻 Features

- Role-based GUI with login (Admin, Technician, Customer)
- Equipment and customer registration
- Repair job creation, assessment, costing, and lifecycle management
- Notifications for job updates and promotional offers
- Management of spare parts, suppliers, and promotions
- Integration with MySQL using JDBC

---

## 🧰 Technologies Used

- Java (Swing GUI)
- MySQL (XAMPP/phpMyAdmin)
- JDBC Connector (MySQL)
- IntelliJ IDEA (Project IDE)
- GitHub (Version Control)

---

## 🗂️ Project Structure

- **Main class**: `EDDTechnologiesSystem.java`
- **Database**: `edd_tec` (set up using phpMyAdmin in XAMPP)
- **GUI Panels**: `LoginPanel`, `AdminPanel`, `TechnicianPanel`, `CustomerPanel`, `RegistrationPanel`
- **Database Connection**: `DBConnection.java`

---

## 🏁 Getting Started

### 1. Prerequisites

- Java 8 or later
- IntelliJ IDEA
- XAMPP (with Apache and MySQL running)
- MySQL JDBC Driver (e.g., `mysql-connector-java-8.0.xx.jar`)

---

### 2. Setting Up the Database

1. Launch XAMPP and start **Apache** and **MySQL**.
2. Open **phpMyAdmin** and import the provided SQL structure into a new database named:
3. Ensure the database includes tables like: `users`, `customers`, `equipment`, `jobs`, `parts`, `notifications`, `suppliers`, and `promotions`.

---

### 3. Setting Up the Project

1. Clone or download the project from GitHub:  
[https://github.com/AnshRRG22/EDD-Technologies-System](https://github.com/AnshRRG22/EDD-Technologies-System)

2. Open the project in **IntelliJ IDEA**.

3. Add the JDBC driver:
- Go to `File` > `Project Structure` > `Libraries`
- Click `+` and add the MySQL `.jar` file (`mysql-connector-java-x.x.xx.jar`)

4. Ensure the database connection config in `DBConnection.java` matches your local setup:
```java
private static final String DB_URL = "jdbc:mysql://localhost:3306/edd_tec";
private static final String DB_USER = "root";
private static final String DB_PASSWORD = ""; // set your MySQL password if any
