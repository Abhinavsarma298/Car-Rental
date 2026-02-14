DriveDock – Car Rental Management System

DriveDock is a microservices-based Car Rental Management System that helps manage cars, bookings, owners, users, payments, and notifications. Built using Spring Boot, Eureka Service Registry, Feign Client, and MySQL, it provides scalable REST APIs for smooth rental operations.

🚀 Features

Microservices architecture with independent services

Service discovery using Eureka Server

Inter-service communication using Feign Client

Car management (CRUD + availability)

Booking management (create booking, cancel booking, booking history)

Owner management and validation

User service support for booking flow

Payment service integration (basic workflow)

Notification service for booking/payment updates

MySQL database integration using Spring Data JPA

🧩 Tech Stack

Backend: Java, Spring Boot, Spring Cloud

Service Registry: Eureka Server

Communication: Feign Client

Database: MySQL

ORM: Spring Data JPA, Hibernate

Build Tool: Maven

Tools: Git, Postman

🏗️ Microservices Included

Eureka Server (Service Registry)

User Service

Owner Service

Car Service

Booking Service

Payment Service

Notification Service

⚙️ Setup Instructions

Clone the repository
git clone https://github.com/Abhinavsarma298/Car-Rental.git

cd Car-Rental

Configure MySQL
Create a database in MySQL:
CREATE DATABASE drivedock;

Update MySQL credentials in each service’s application.properties if needed:
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
spring.jpa.hibernate.ddl-auto=update

▶️ Run Order (Important)
Start services in this order:

Eureka Server

User Service

Owner Service

Car Service

Booking Service

Payment Service

Notification Service

Run each service using:
mvn spring-boot:run

🔍 Eureka Dashboard
After starting Eureka Server, open:
http://localhost:8761

You will see all microservices registered there.

📂 API Endpoints

Car Service

GET /cars/all → Get all cars

GET /cars/available → Get available cars

POST /cars/add → Add a new car

Booking Service

POST /bookings/create → Create a booking

PUT /bookings/cancel/{bookingId} → Cancel booking

GET /bookings/history/{userId} → Booking history

Payment Service

POST /payments/pay → Make payment

GET /payments/status/{bookingId} → Payment status

Notification Service

POST /notifications/send → Send notification

GET /notifications/{userId} → Get notifications

🧠 Author
Y V Abhinav Kumar Sarma
Full Stack Developer | Java | Spring Boot | Microservices | MySQL
LinkedIn: https://www.linkedin.com/in/abhinav-fullstack-dev

GitHub: https://github.com/Abhinavsarma298
