# 🛡️ Defensy Backend
> **A Security Operations Center (SOC) platform backend with MongoDB, Elasticsearch, and Keycloak.**

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.3-brightgreen?style=flat&logo=spring)
![Java](https://img.shields.io/badge/Java-21-blue?style=flat&logo=openjdk)
![MongoDB](https://img.shields.io/badge/MongoDB-6.0-green?style=flat&logo=mongodb)
![Elasticsearch](https://img.shields.io/badge/Elasticsearch-8.x-orange?style=flat&logo=elasticsearch)
![Keycloak](https://img.shields.io/badge/Keycloak-Not%20yet%20implemented-lightgrey?style=flat&logo=keycloak)
![Docker](https://img.shields.io/badge/Docker-Compose-blue?style=flat&logo=docker)

## 📌 Overview
Defensy Backend is a **modular and scalable SOC platform** built using **Spring Boot Modulith**.  
It integrates **MongoDB for user data**, **Elasticsearch for audit logs**, and supports **event-driven architecture**.

---

## 📖 Table of Contents
- [🚀 Features](#-features)
- [🛠️ Technologies Used](#-technologies-used)
- [📂 Project Modules](#-project-modules)
- [🌐 API Endpoints](#-api-endpoints)
- [🔧 Setup and Installation](#-setup-and-installation)
- [📌 Future Enhancements](#-future-enhancements)
- [🤝 Contributing](#-contributing)

---

## 🚀 Features
✅ **User Management** (CRUD operations with MongoDB)  
✅ **Audit Logging** (Stored in MongoDB & Elasticsearch)  
✅ **Spring Modulith Architecture** (Event-driven services)  
✅ **REST API Implementation** (Spring Web, ResponseEntity)  
✅ **Logging & Monitoring** (SLF4J, Event-based logging)  
✅ **Docker Compose Integration** (One-command deployment)  
⏳ **Security with Keycloak** *(Planned feature for authentication)*  

---

## 🛠️ Technologies Used
| Technology              | Purpose                                      |
|-------------------------|----------------------------------------------|
| **Java 21**            | Core backend development                     |
| **Spring Boot 3.4.3**  | REST API & Modular Architecture              |
| **Spring Modulith**    | Event-driven service structure               |
| **MongoDB**           | Primary database (User & Audit Logs)         |
| **Elasticsearch**      | Storing & retrieving audit logs              |
| **Gradle Kotlin DSL**  | Dependency Management                        |
| **Docker Compose**     | Multi-container deployment                   |
| **Keycloak**          | Planned for Authentication                   |

---

## 📂 Project Modules

### 🔹 **User Module (`/users`)**
Handles **user CRUD operations**, broadcasting events for auditing.

### 🔹 **Audit Logging Module (`/audit`)**
Captures **user actions** and stores them in **MongoDB & Elasticsearch**.

### 🔹 **Event-Driven Architecture**
📢 Uses **Spring Modulith** to handle **asynchronous user activity logging**.

---

## 🌐 API Endpoints

### 🟢 **User Module** (`/users`)
| Method   | Endpoint       | Description               |
|----------|---------------|---------------------------|
| `GET`    | `/users`       | Retrieve all users       |
| `GET`    | `/users/{id}`  | Retrieve user by ID      |
| `POST`   | `/users`       | Create a new user        |
| `PUT`    | `/users/{id}`  | Update user details      |
| `DELETE` | `/users/{id}`  | Delete user by ID        |

### 🟢 **Audit Log Module** (`/audit`)
| Method   | Endpoint               | Description                          |
|----------|------------------------|--------------------------------------|
| `GET`    | `/audit/mongo`         | Get all MongoDB audit logs          |
| `GET`    | `/audit/mongo/{id}`    | Get MongoDB audit log by ID         |
| `GET`    | `/audit/elastic`       | Get all Elasticsearch logs          |
| `GET`    | `/audit/elastic/{id}`  | Get Elasticsearch log by ID         |
| `POST`   | `/audit/mongo`         | Create MongoDB audit log            |
| `POST`   | `/audit/elastic`       | Create Elasticsearch audit log      |

---

## 🔧 Setup and Installation

### **1️⃣ Clone the Repository**
```sh
git clone https://github.com/YOUR-USERNAME/defensy-backend.git
cd defensy-backend
