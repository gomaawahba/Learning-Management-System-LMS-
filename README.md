# 📚 Learning Management System (LMS) - Backend

A **Learning Management System (LMS)** backend built using **Java Spring Boot**.  
This system provides APIs for managing courses, users, authentication, assignments, and learning progress with secure and scalable architecture.

---

## 🚀 Tech Stack

- ☕ Java 21
- 🌱 Spring Boot 3
- 🗄️ Spring Data JPA
- 🔐 Spring Security + JWT Authentication
- 🐘 PostgreSQL
- ✈️ Flyway Database Migration
- 📄 OpenAPI / Swagger
- 🧠 Spring AI (Mistral AI integration)
- 📦 Lombok
- 🧩 MapStruct
- 📁 Maven

---

## 🏗️ Architecture

This project follows a **layered architecture**:

---

## 🔐 Security

- JWT-based authentication
- Role-based access control (Student / Instructor / Admin)
- Secure REST APIs using Spring Security

---

## 🗄️ Database Migration

This project uses **Flyway** for database versioning and migration.

- Migrations located in:
- 
- Automatically executed on application startup
- Ensures consistent database schema across environments

---

## 🤖 AI Integration

The system integrates **Spring AI (Mistral AI)** to support intelligent features such as:
- Smart content generation
- AI-assisted learning features
- Chat-based educational support

---

## ⚙️ Configuration (application.yml)

Key configurations include:

- PostgreSQL database connection
- JWT secret & expiration
- File upload limits
- Flyway migration settings
- AI model configuration

---

## 📦 Features

### 👨‍🎓 Students
- Register / Login
- View courses
- Submit assignments
- Track progress

### 👨‍🏫 Instructors
- Create courses
- Upload materials
- Manage assignments
- Evaluate students

### 🛠️ Admin
- Manage users
- Control system data
- Monitor platform activity

---
🚀 Running the Project
1. Clone repository
git clone https://github.com/gomaawahba/Learning-Management-System-LMS-.git
cd Learning-Management-System-LMS-

2. Setup database
CREATE DATABASE lmsdb;
3. Run application
mvn spring-boot:run
