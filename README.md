# 💍 WeddingHub — Wedding Venue & Event Services Booking Platform

WeddingHub is a full-stack wedding venue and event services booking platform designed to simplify wedding planning. It brings together customers, wedding hall vendors, catering services, and decoration services in one place.

Customers can explore available marriage halls and event packages, manage bookings, and track their booking and payment status. Vendors can manage their services, while administrators oversee platform operations and vendor approvals.

The project is built using Java, Spring Boot, Spring Security, MySQL, and a React-based frontend.

---

## 📌 Table of Contents

- [Project Overview](#-project-overview)
- [Key Features](#-key-features)
- [Technology Stack](#-technology-stack)
- [System Architecture](#-system-architecture)
- [User Roles](#-user-roles)
- [Getting Started](#-getting-started)
- [Backend Setup](#-backend-setup)
- [Database Configuration](#-database-configuration)
- [Frontend Setup](#-frontend-setup)
- [Authentication and Login](#-authentication-and-login)
- [Application Workflow](#-application-workflow)
- [API Overview](#-api-overview)
- [Testing](#-testing)
- [Troubleshooting](#-troubleshooting)
- [Security Considerations](#-security-considerations)
- [Future Improvements](#-future-improvements)
- [Author](#-author)

---

## 🎯 Project Overview

Planning a wedding often requires coordinating multiple services, including venue reservations, catering, and decoration.

WeddingHub aims to simplify this process through a centralized platform where customers can discover venues and services, make bookings, and manage their event arrangements.

The application follows a layered backend architecture, separating HTTP request handling, business logic, database access, and security responsibilities.

### Project Goals

- Simplify marriage hall discovery and booking.
- Centralize wedding-related services.
- Provide separate functionality for customers, vendors, and administrators.
- Implement secure authentication and role-based authorization.
- Maintain booking and payment records.
- Provide a maintainable foundation for future enhancements.

---

## ✨ Key Features

### 🏛️ Marriage Hall Management

- Browse available marriage halls.
- Search halls using supported filters.
- View individual hall details.
- Display hall information such as location, capacity, and pricing.
- Support hall availability and status management.

### 📦 Catering and Decoration Packages

- Organize catering packages.
- Organize decoration packages.
- Display available event service options.
- Associate selected services with bookings where supported by the application.

### 📅 Booking Management

- Create and manage wedding bookings.
- Maintain booking records and booking statuses.
- Associate catering and decoration selections with bookings.
- Support booking history and cancellation where implemented.

### 🔐 Authentication and Authorization

- User registration and login.
- Password-based authentication.
- Spring Security integration.
- User identity management through `WeddingHubPrincipal`.
- User loading through `WeddingHubUserDetailsService`.
- Role-based access for customers, vendors, and administrators.

### 🧑‍💼 Vendor Management

- Vendor profiles.
- Vendor approval status management.
- Vendor-related service management.
- Administrative vendor management.

### 🛡️ Administration

- Administrative APIs.
- Vendor management and approval workflows.
- Platform management functionality according to assigned permissions.

### 💳 Payment Management

- Payment records.
- Payment methods and payment statuses.
- Association of payment information with bookings.

> **Payment note:** The presence of payment entities does not necessarily mean a real payment gateway is integrated. Check the implementation before assuming that card, UPI, or other live payments are supported.

---

## 🛠️ Technology Stack

The following technologies are identified from the project structure and configuration. Confirm the exact dependency versions in `pom.xml`.

| Technology | Purpose |
|---|---|
| Java 21 | Backend programming language |
| Spring Boot | Backend application framework |
| Spring Web | REST API development |
| Spring Data JPA | Database access and persistence |
| Hibernate | ORM implementation |
| Spring Security | Authentication and authorization |
| MySQL | Relational database |
| Maven | Dependency management and build automation |
| React | Frontend user interface, if included in the repository |
| Git and GitHub | Version control and source-code hosting |
| Docker | Containerization, if configured |
| Jenkins | CI/CD automation, if configured |
| AWS EC2 | Cloud deployment, if configured |

---

## 🏗️ System Architecture

WeddingHub uses a layered backend architecture.

```text
                    ┌─────────────────────┐
                    │   React Frontend    │
                    │  User Interface     │
                    └──────────┬──────────┘
                               │
                               │ HTTP / REST
                               ▼
                    ┌─────────────────────┐
                    │  Spring Security    │
                    │ Authentication      │
                    │ Authorization       │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │    Controllers      │
                    │ REST API Endpoints  │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │     Services        │
                    │ Business Logic      │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │    Repositories     │
                    │ Spring Data JPA     │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │       MySQL         │
                    │  Persistent Data    │
                    └─────────────────────┘
```

### Backend Package Structure

The project contains the following major components:

```text
src/
└── main/
    ├── java/
    │   └── com/
    │       └── weddinghub/
    │           ├── config/
    │           ├── Controller/
    │           ├── dto/
    │           ├── entity/
    │           ├── repository/
    │           ├── security/
    │           ├── service/
    │           └── WeddinghubApplication.java
    │
    └── resources/
        ├── static/
        ├── templates/
        └── application.properties
```

The exact package names and capitalization should match the source code.

### Important Components

**Controllers**

Handle HTTP requests and return responses.

Examples visible in the project include:

- `AuthController`
- `BookingController`
- `MarriageHallController`
- `VendorController`
- `AdminController`
- `PublicPackageController`

**Services**

Implement business operations and coordinate repositories.

Examples include:

- `AuthService`
- `AdminService`
- `VendorManagementService`

**Repositories**

Provide database access using Spring Data JPA.

Examples include:

- `UserRepository`
- `MarriageHallRepository`
- `BookingRepository`
- `VendorProfileRepository`
- `PaymentRepository`

**Entities**

Represent persistent application data, including users, marriage halls, bookings, packages, vendors, and payments.

**Security**

The security package includes:

- `WeddingHubPrincipal`
- `WeddingHubUserDetailsService`

Security configuration and exception handling are also part of the application.

---

## 👥 User Roles

WeddingHub is designed around three primary user roles.

### 1. Customer

Customers use the platform to discover wedding services and manage their event arrangements.

Typical customer workflow:

1. Register an account.
2. Log in.
3. Browse available marriage halls.
4. Search for a suitable venue.
5. Explore catering and decoration options.
6. Create a booking.
7. Review booking and payment status.
8. Manage bookings using the available operations.

### 2. Vendor

Vendors provide wedding-related services through the platform.

Depending on the implemented permissions, vendors can:

- Maintain vendor profile information.
- Submit information for approval.
- Manage associated halls or services.
- Review relevant bookings.
- Maintain service information.

Vendor approval requirements should be followed before attempting vendor-only operations.

### 3. Administrator

Administrators manage platform operations.

Administrative capabilities may include:

- Reviewing vendor applications.
- Approving or rejecting vendors.
- Managing vendor records.
- Accessing administrative functionality.
- Maintaining platform operations.

The server must enforce role restrictions. Hiding a button in the frontend is not sufficient to secure an administrative operation.

---

## 🚀 Getting Started

### Prerequisites

Install the software required by the parts of the project you want to run.

- Java Development Kit 21
- Maven, or the included Maven Wrapper
- MySQL Server
- Git
- Node.js and npm if a separate React frontend is included
- Docker if container-based execution is configured

Verify your installations:

```bash
java -version
```

```bash
mvn -version
```

```bash
git --version
```

For the frontend:

```bash
node --version
npm --version
```

---

## 📥 Clone the Repository

Replace the placeholder with your actual GitHub repository URL.

```bash
git clone <YOUR_GITHUB_REPOSITORY_URL>
```

Navigate into the project:

```bash
cd weddinghub
```

If the frontend is maintained in a separate repository, clone that repository separately.

---

## ⚙️ Backend Setup

### Step 1: Configure the Database

Start your MySQL server and create the application database.

```sql
CREATE DATABASE weddinghub_db;
```

If your database already exists and contains your project tables, do not recreate it unnecessarily.

### Step 2: Configure Environment Variables

The application configuration supports environment variables for the database connection.

Configure the following variables in your environment:

| Variable | Description |
|---|---|
| `WEDDINGHUB_DB_URL` | JDBC URL of the MySQL database |
| `WEDDINGHUB_DB_USERNAME` | MySQL username |
| `WEDDINGHUB_DB_PASSWORD` | MySQL password |

Example values for a local development environment:

```text
WEDDINGHUB_DB_URL=jdbc:mysql://localhost:3306/weddinghub_db
WEDDINGHUB_DB_USERNAME=root
WEDDINGHUB_DB_PASSWORD=<YOUR_MYSQL_PASSWORD>
```

These are example configuration values, not production credentials.

Do not commit real passwords, API keys, tokens, or other secrets to GitHub.

### Step 3: Understand the Application Configuration

The current configuration includes settings similar to:

```properties
spring.application.name=weddinghub
server.port=8081

spring.datasource.url=${WEDDINGHUB_DB_URL:jdbc:mysql://localhost:3306/weddinghub_db}
spring.datasource.username=${WEDDINGHUB_DB_USERNAME:root}
spring.datasource.password=${WEDDINGHUB_DB_PASSWORD}

spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=false
spring.jpa.open-in-view=false
```

Check the actual `application.properties` file for the complete configuration.

**Database schema note:** `spring.jpa.hibernate.ddl-auto=none` means Hibernate will not automatically create or update the database schema. The required tables must already exist or be created through the project's database scripts or migrations.

### Step 4: Build the Backend

From the directory containing `pom.xml`, run:

```bash
mvn clean package
```

If the repository includes the Maven Wrapper, you can use:

**Windows:**

```powershell
.\mvnw.cmd clean package
```

**Linux/macOS:**

```bash
./mvnw clean package
```

### Step 5: Run the Application

Using Maven:

```bash
mvn spring-boot:run
```

Or, if using the Maven Wrapper:

**Windows:**

```powershell
.\mvnw.cmd spring-boot:run
```

**Linux/macOS:**

```bash
./mvnw spring-boot:run
```

The screenshots show `server.port=8081`, so the backend is configured to use port `8081`.

Check the startup logs for confirmation that Spring Boot has started successfully and connected to MySQL.

---

## 🗄️ Database Configuration

WeddingHub uses MySQL to store application data.

The project includes entities and repositories related to:

- Users
- User roles
- Marriage halls
- Hall statuses
- Vendor profiles
- Vendor approval statuses
- Bookings
- Booking statuses
- Catering packages
- Decoration packages
- Booking catering selections
- Booking decoration selections
- Payments
- Payment methods
- Payment statuses

The exact database table names, column definitions, foreign keys, and constraints should be taken from the project's actual SQL schema.

### Database Setup Checklist

- [ ] MySQL is running.
- [ ] The `weddinghub_db` database exists.
- [ ] All required tables have been created.
- [ ] Environment variables are configured correctly.
- [ ] Foreign-key relationships match the application entities.
- [ ] Required initial data is available, if the application depends on it.
- [ ] The database user has the necessary permissions.

Do not insert guessed user records directly into the database. Registration and administrative bootstrap procedures must follow the application's implemented password-hashing and role-assignment logic.

---

## 🖥️ Frontend Setup

If the React frontend is included in the repository, navigate to its directory.

For example, if the frontend is stored in a directory named `frontend`:

```bash
cd frontend
```

Install dependencies:

```bash
npm install
```

Configure the backend API base URL using the environment-variable convention expected by the frontend.

For a Vite application, the variable might follow this pattern:

```text
VITE_API_BASE_URL=http://localhost:8081
```

Use the actual variable name expected by the project's API client.

Start the development server:

```bash
npm run dev
```

The frontend development server will display its URL in the terminal.

If the frontend is maintained separately, follow the setup instructions in its own repository.

### Frontend and Backend Communication

The frontend sends HTTP requests to the Spring Boot backend.

For local development, ensure:

- The backend is running.
- The frontend points to the correct backend URL.
- CORS allows the frontend's development origin.
- The API endpoints match the backend implementation.
- Authentication credentials are sent according to the configured authentication mechanism.

---

## 🔐 Authentication and Login

WeddingHub includes an authentication module built around Spring Security.

The backend contains `AuthController`, `AuthService`, `WeddingHubPrincipal`, and `WeddingHubUserDetailsService`.

### First-Time User

If you do not already have an account:

1. Start the backend and frontend.
2. Open the frontend in your browser.
3. Navigate to the registration page, if implemented.
4. Enter the required registration information.
5. Submit the registration form.
6. Follow the application's response and login flow.
7. Log in using the credentials you registered.

Use the actual field names and validation requirements shown in the registration form.

### Existing User

1. Open the WeddingHub frontend.
2. Navigate to the login page.
3. Enter your registered email address and password.
4. Submit the login form.
5. Follow the authentication response provided by the application.

The exact login endpoint, request body, and response structure depend on the implementation in `AuthController` and `AuthService`.

### Demo Login Credentials

**No verified demo credentials are documented here.**

Do not assume that a default administrator, vendor, or customer account exists.

If the project includes an administrator bootstrap component, inspect `AdminBootstrap` to determine whether an administrator is created automatically, which configuration it uses, and how its credentials are supplied.

Never publish a real administrator password in a public README.

### Customer Access

A customer should register through the application's supported registration flow and log in with the resulting account.

### Vendor Access

Vendor accounts may require approval before vendor operations are available. Complete the vendor registration or application workflow and follow the application's approval rules.

### Administrator Access

Administrator access must be provisioned through the application's configured bootstrap or administrative process. Do not assume that registering a normal account grants administrator permissions.

---

## 🔄 Application Workflow

A typical WeddingHub workflow is:

```text
                Open WeddingHub
                       |
                       v
              Browse Public Halls
                       |
                       v
              Search for a Venue
                       |
                       v
              View Hall Details
                       |
                       v
                 Register/Login
                       |
                       v
              Select Event Services
                       |
                       v
                Create Booking
                       |
                       v
              Review Booking Status
                       |
                       v
               Review Payment Status
```

Vendor and administrator workflows are separate and depend on the permissions assigned to each account.

---

## 🔌 API Overview

The backend contains controllers for authentication, bookings, marriage halls, vendors, administration, and public packages.

The following table identifies controller groups visible in the project structure. Exact routes and HTTP methods must be verified against the controller annotations.

| Controller | Purpose |
|---|---|
| `AuthController` | Authentication and account operations |
| `MarriageHallController` | Marriage hall browsing and search |
| `BookingController` | Booking-related operations |
| `VendorController` | Vendor operations |
| `AdminController` | Administrative operations |
| `PublicPackageController` | Public package-related operations |

### Marriage Hall API

The previously implemented hall controller uses the `/api/v1/halls` route.

The known endpoint patterns are:

| HTTP Method | Endpoint | Purpose |
|---|---|---|
| GET | `/api/v1/halls` | Retrieve available halls |
| GET | `/api/v1/halls/search` | Search halls using supported filters |
| GET | `/api/v1/halls/{id}` | Retrieve details for a hall |

Example:

```http
GET /api/v1/halls
```

Example search:

```http
GET /api/v1/halls/search?city=Chennai&minimumCapacity=300&maximumPrice=100000
```

Example individual hall request:

```http
GET /api/v1/halls/1
```

These examples reflect the hall controller previously shared in the project conversation. Verify them against the current source before relying on them.

### API Testing

Use Postman or another HTTP client to test the API.

For each endpoint, verify:

- HTTP method
- URL and path variables
- Query parameters
- Request body
- Authentication requirements
- Required roles
- Success response
- Error response
- Database changes, where applicable

Do not assume that every endpoint is public. Follow the authorization rules in `SecurityConfig`.

---

## 🧪 Testing

Run the backend tests with Maven:

```bash
mvn test
```

Or use the Maven Wrapper:

**Windows:**

```powershell
.\mvnw.cmd test
```

**Linux/macOS:**

```bash
./mvnw test
```

Build the project and run the configured verification steps:

```bash
mvn verify
```

The actual test coverage depends on the test classes present in the repository.

### Recommended Test Scenarios

#### Authentication

- Valid registration.
- Duplicate email registration.
- Valid login.
- Invalid password.
- Unknown account.
- Unauthorized access.
- Role-restricted access.

#### Marriage Halls

- Retrieve available halls.
- Search by city.
- Filter by capacity.
- Filter by maximum price.
- Request a valid hall ID.
- Request a nonexistent hall.
- Reject invalid query parameters.

#### Bookings

- Create a valid booking.
- Reject invalid booking details.
- Handle unavailable dates, if availability checks are implemented.
- Retrieve booking information.
- Test cancellation and status transitions where supported.
- Verify that users cannot access another user's protected booking information.

#### Vendors and Administration

- Verify vendor approval rules.
- Verify vendor permissions.
- Verify administrator-only operations.
- Ensure ordinary users cannot grant themselves privileged roles.

#### Payments

- Verify payment status transitions.
- Test invalid payment references.
- Confirm that failed payments do not incorrectly become successful.

---

## 🐛 Troubleshooting

### MySQL Connection Error

Check that:

- MySQL is running.
- The database URL is correct.
- The username and password are valid.
- The database exists.
- The configured database port is correct.

### Missing Table Error

The project uses:

```properties
spring.jpa.hibernate.ddl-auto=none
```

Hibernate will not automatically create missing tables.

Create the schema using the project's database scripts or migrations.

### Port Already in Use

The backend is configured for port `8081`.

If another application occupies that port, stop the conflicting application or change the configured port.

### CORS Error

Check that the frontend origin matches the allowed origin configured in the backend.

Also verify the allowed methods, request headers, and CORS handling in Spring Security.

CORS errors cannot be resolved merely by disabling authentication.

### Login Failure

Check:

- The account exists.
- The email address is correct.
- The password is correct.
- The stored password was encoded as expected.
- The authentication service is configured correctly.
- The account's role and status meet the application's requirements.

### Access Denied

Check the security configuration and the user's assigned role.

An authenticated user is not automatically authorized to perform every operation.

---

## 🔒 Security Considerations

- Keep database credentials out of source control.
- Use password hashing rather than storing plaintext passwords.
- Validate incoming request data.
- Enforce authorization on the backend.
- Restrict administrative operations to authorized administrators.
- Apply vendor approval rules consistently.
- Protect sensitive user and booking information.
- Use HTTPS in production.
- Configure CORS for trusted frontend origins.
- Review CSRF settings according to the authentication mechanism.
- Keep dependencies updated.
- Never commit private keys, access tokens, or production secrets.
- Do not expose privileged demo credentials in public documentation.

If JWT authentication is used, verify token validation, expiration handling, signing-key management, and authorization behavior against the implementation.

---

## 🚀 Deployment

The backend can be packaged as a JAR using Maven:

```bash
mvn clean package
```

Spring Boot projects typically produce a runnable JAR under the `target/` directory.

The exact filename depends on the Maven configuration.

If the repository contains Docker, Docker Compose, Jenkins, or AWS deployment configuration, follow the commands and environment-variable requirements documented in those files.

Before deploying:

1. Configure production database credentials.
2. Ensure the database schema is initialized.
3. Configure the frontend's production API URL.
4. Restrict CORS to the production frontend origin.
5. Review authentication and authorization.
6. Disable development-only settings.
7. Configure HTTPS.
8. Confirm logs do not expose secrets.
9. Run tests.
10. Verify the deployed application using real HTTP requests.

Do not expose a development database or privileged account directly to the public internet.

---

## 🔮 Future Improvements

Potential enhancements include:

- Real payment gateway integration.
- Email verification and password recovery.
- Booking notifications.
- Advanced venue availability calendars.
- Image upload and cloud storage.
- Reviews and ratings.
- Advanced search and pagination.
- Audit logging for administrative operations.
- Automated integration testing.
- Continuous deployment pipelines.
- Production monitoring and observability.

These are potential improvements, not claims that the features are currently implemented.

---

## 👨‍💻 Author

**Akash S**

Computer Science / Software Development

GitHub: [AndrewAkash](https://github.com/andrewakash)

LinkedIn: [Akash S](https://www.linkedin.com/in/akash-andrew/)

---

## ⭐ Support

If you find this project useful, consider giving the repository a star on GitHub.

Thank you for checking out WeddingHub! 💍
