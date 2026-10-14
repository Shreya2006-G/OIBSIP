Task 1 project files 

# Little Journeys — Online Reservation System

A Java Swing desktop application for railway reservation management, developed as part of the **Oasis Infobyte Internship Program (OIBSIP)**.

## Overview

Little Journeys allows users to register, log in, search for trains, book reservations, view booking details, and cancel reservations through a graphical interface.

## Features

- User registration and login
- Train search by source and destination
- Connecting journey suggestions
- Passenger details and seat allocation
- PNR generation and booking confirmation
- View and manage reservations
- Group and individual passenger cancellation where supported
- SQLite database integration

## Technology Stack

- **Language:** Java 21
- **GUI:** Java Swing
- **Database:** SQLite
- **Connectivity:** JDBC
- **Build Tool:** Maven
- **IDE:** IntelliJ IDEA

## Project Structure

```text
Java-Task1-OnlineReservationSystem/
├── src/
│   └── main/
│       ├── java/
│       └── resources/
│           └── images/
├── OnlineReservation.db
├── pom.xml
└── README.md
```

## Prerequisites

- JDK 21
- IntelliJ IDEA
- Maven

## How to Run

1. Download or clone the `OIBSIP` repository.
2. Open the `Java-Task1-OnlineReservationSystem` folder in IntelliJ IDEA.
3. Load and resolve the Maven dependencies.
4. Ensure `OnlineReservation.db` is in the project root and contains the required tables and sample data.
5. Run `LoginFrame.java` using its `main()` method.

## Database

The application uses SQLite to store user accounts, train information, station details, reservations, passenger records, and seat assignments.

`DatabaseInitializer.java` initializes the required tables and inserts sample station and train records when needed.

## Important Note

This project uses local demonstration data and is not connected to live Indian Railways schedules or booking services.

## Developer

**Shreya Gandewar**

**Internship Track:** Java Development  
**Program:** Oasis Infobyte Internship (OIBSIP)

