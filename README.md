# OIBSIP
Oasis Infobyte Internship Tasks
# Little Journeys — Online Reservation System

A Java-based desktop application for railway reservation management, built using Java Swing and SQLite.

Developed as part of the **Oasis Infobyte Internship Program (OIBSIP)**.

## Overview

Little Journeys provides a graphical interface for managing railway reservations, from user registration and train search to ticket booking and cancellation. The project demonstrates Java application development, JDBC connectivity, and relational database operations.

## Features

- **User Authentication:** Registration and login.
- **Train Search:** Search by source and destination.
- **Connecting Journeys:** Find eligible two-train connections.
- **Ticket Booking:** Enter passenger details and select journey preferences.
- **PNR Generation:** Generate a unique reservation reference.
- **Seat Allocation:** Assign available seats based on journey details.
- **Booking Management:** View bookings and cancel reservations.
- **Passenger Management:** Cancel individual passengers where supported.

## Technology Stack

| Technology | Purpose |
|---|---|
| Java 21 | Application logic |
| Java Swing | Graphical user interface |
| SQLite | Database |
| JDBC | Database connectivity |
| Maven | Dependency management |
| IntelliJ IDEA | Development environment |

## Demo Stations

The application includes 34 sample stations:

Mumbai CSMT, Mumbai Central, Pune, Nagpur Junction, Nashik Road, Ahmedabad Junction, New Delhi, Bandra Terminus, Dadar, Lokmanya Tilak Terminus, KSR Bengaluru, Chennai Central, Hyderabad Deccan, Howrah Junction, Jaipur Junction, Bhopal Junction, Lucknow Charbagh, Madgaon, Solapur, Thiruvananthapuram Central, Surat, Vadodara Junction, Rajkot, Agra Cantt, Amritsar Junction, Patna Junction, Bhubaneswar, Ernakulam Junction, Mysuru Junction, Kota Junction, Jabalpur, Gwalior Junction, Varanasi Junction, and Prayagraj Junction.

## Sample Connecting Journeys

| Journey | Interchange |
|---|---|
| Pune → Surat → Mysuru Junction | Surat |
| Pune → Mumbai Central → Ahmedabad | Mumbai Central |
| Pune → Nagpur Junction → Bhopal Junction | Nagpur Junction |
| Pune → Solapur → Hyderabad Deccan | Solapur |
| New Delhi → Jaipur Junction → Kota Junction | Jaipur Junction |
| New Delhi → Agra Cantt → Gwalior Junction | Agra Cantt |
| Howrah Junction → Bhubaneswar → Chennai Central | Bhubaneswar |
| Mumbai Central → Vadodara Junction → Rajkot | Vadodara Junction |

*Connecting journeys are generated from matching source and destination station names in the sample database. Results depend on the available train records.*

## Project Structure

```text
OIBSIP/
├── src/
│   └── main/
│       ├── java/
│       └── resources/
│           └── images/
├── OnlineReservation.db
├── pom.xml
├── .gitignore
└── README.md
```

## Getting Started

### Prerequisites

- JDK 21
- IntelliJ IDEA
- Maven

### Run Locally

1. Clone or download this repository.
2. Open the project in IntelliJ IDEA.
3. Load the Maven dependencies.
4. Ensure `OnlineReservation.db` exists in the project root and contains the required tables and sample data.
5. Run `LoginFrame.java` using its `main()` method.

`DatabaseInitializer.java` can be used to create the required tables and insert sample station and train records.

## Database

SQLite stores user accounts, train information, stations, reservations, passenger details, and seat assignments. JDBC is used to interact with the database.

## Limitations

- Train schedules and availability are based on local sample data.
- The application is not connected to live Indian Railways services.
- Connecting journeys are based on station matching and do not guarantee valid transfer timings.

## Developer

**Shreya Gandewar**

**Project:** Little Journeys — Online Reservation System  
**Program:** Oasis Infobyte Internship (OIBSIP)

