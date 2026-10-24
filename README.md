# WeddingHub

WeddingHub is a student project for browsing wedding venues and packages and managing bookings. It has a Spring Boot REST API and a React/Vite web client. The backend uses the existing MySQL `weddinghub_db` schema; Hibernate is configured not to create or alter tables.

## Requirements

- Java 21
- MySQL with the `weddinghub_db` schema loaded
- Node.js and npm

## Run locally

Start MySQL first. From the project root, run the backend helper and enter the local database password and the first admin account credentials when prompted:

```powershell
.\scripts\run-local.ps1
```

The API listens on `http://localhost:8081`. Start the frontend in a second PowerShell window:

```powershell
cd frontend
npm install
npm run dev
```

Vite serves the frontend on `http://localhost:5173`. `WEDDINGHUB_DB_URL` and `WEDDINGHUB_DB_USERNAME` can be set before running the helper if your local MySQL connection differs from `localhost:3306` and `root`.

## Current features

- Customer and vendor registration with BCrypt password hashing
- Role-protected customer, vendor, and admin API routes
- Vendor approval and listing management
- Public venue search by city, minimum capacity, and maximum price
- Booking, availability checks, optional catering and decoration, and demo payment reconciliation
- CSRF protection for state-changing requests

Payments are demo-only; no payment provider is connected. API routes and additional setup details are in [HELP.md](HELP.md).

## Database and credentials

The project expects the nine existing tables in `weddinghub_db`: users, vendor profiles, marriage halls, catering and decoration packages, bookings, booking details, and payments. Do not commit local passwords or `.env` files. The helper reads credentials interactively and only passes them to the local backend process.
