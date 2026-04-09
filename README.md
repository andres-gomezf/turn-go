# TurnGo API
REST API for managing sports court bookings, built with Spring Boot, JPA, and PostgreSQL.
## Table of Contents
- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Domain Model](#domain-model)
- [Requirements](#requirements)
- [Environment Variables](#environment-variables)
- [Local Setup](#local-setup)
- [Run with Docker (Database only)](#run-with-docker-database-only)
- [API Endpoints](#api-endpoints)
- [Email Notifications](#email-notifications)
- [Error Handling](#error-handling)
- [Testing](#testing)
- [Useful Commands](#useful-commands)
- [Security Status](#security-status)
- [Roadmap](#roadmap)
- [Contributing](#contributing)
- [License](#license)
---
## Overview
TurnGo allows you to:
- manage clients
- manage courts
- manage court schedules
- create and cancel bookings
- check availability by date
- optionally send booking confirmation emails via request header
It is designed as a backend API for a booking flow (for example, consumed by an Angular frontend).
---
## Tech Stack
- **Java 23**
- **Spring Boot 4 (Milestone)**
  - Spring Web
  - Spring Data JPA
  - Spring Security
  - Spring Validation
  - Spring Mail
- **PostgreSQL**
- **Maven**
- **Lombok**
- **Testcontainers + JUnit 5 + Spring Test**
---
## Architecture
Main package structure:
- `controllers/`: REST endpoints
- `services/`: business logic
- `repositories/`: data access layer
- `entities/`: JPA entities
- `dtos/`: request/response contracts
- `exceptions/`: global exception handling
- `security/`: security configuration
---
## Domain Model
Core entities:
- **Cliente (Client)**
  - `id`, `nombre`, `apellido`, `correo` (unique), `userId`
- **Cancha (Court)**
  - `id`, `numero` (unique)
- **Horario (Schedule Slot)**
  - `id`, `cancha`, `horaInicio`, `horaFin`
- **Turno (Booking)**
  - `id`, `cliente`, `horario`, `fechaInicio`, `estado`
- **EstadoReserva (Booking Status)**
  - enum (e.g. `RESERVADA`)
Key business rule: duplicate bookings for the same schedule slot and date are not allowed (`409 Conflict`).
---
## Requirements
- Java 23
- Maven 3.9+
- PostgreSQL 14+ (or Docker)
- Docker Desktop (recommended for integration tests with Testcontainers)
---
