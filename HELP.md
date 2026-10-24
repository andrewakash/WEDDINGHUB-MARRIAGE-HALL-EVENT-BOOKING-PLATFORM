# WeddingHub backend

WeddingHub uses the existing `weddinghub_db` schema. Hibernate schema generation is disabled; the application does not create, drop, or alter tables.

## Run locally

Set the database password and a one-time admin bootstrap credential in PowerShell, then start Spring Boot:

```powershell
$env:WEDDINGHUB_DB_PASSWORD = "<your MySQL password>"
$env:WEDDINGHUB_ADMIN_EMAIL = "admin@example.com"
$env:WEDDINGHUB_ADMIN_PASSWORD = "<a unique password of 12–72 UTF-8 bytes>"
$env:WEDDINGHUB_ADMIN_FULL_NAME = "WeddingHub Admin"
.\mvnw.cmd spring-boot:run
```

`WEDDINGHUB_DB_URL` and `WEDDINGHUB_DB_USERNAME` are optional; they default to `jdbc:mysql://localhost:3306/weddinghub_db` and `root`. The admin account is created only when the configured email is not already present. Admin self-registration is disabled.

## API authentication and CSRF

Protected endpoints use HTTP Basic authentication with the account email and password. Passwords created through the registration endpoints are stored using BCrypt. Do not transmit Basic credentials outside local development without HTTPS.

All state-changing requests require a CSRF token. First call `GET /api/v1/auth/csrf`, keep the `XSRF-TOKEN` cookie, and send the returned token in the `X-XSRF-TOKEN` header on `POST`, `PUT`, and `PATCH` requests. The API permits browser origins `http://localhost:5173` and `http://localhost:4173`.

## Main endpoints

| Access | Endpoints |
| --- | --- |
| Public | `GET /api/v1/home`, `GET /api/v1/halls`, `GET /api/v1/halls/{id}`, `GET /api/v1/halls/search?city=&minimumCapacity=&maximumPrice=` |
| Public | `GET /api/v1/catering-packages[/{id}]`, `GET /api/v1/decoration-packages[/{id}]` |
| Public | `POST /api/v1/auth/register`, `POST /api/v1/auth/vendors/register` |
| Signed in | `GET /api/v1/auth/me`; customer booking endpoints under `/api/v1/bookings` |
| Approved vendor | `/api/v1/vendor/profile`, `/halls`, `/catering-packages`, `/decoration-packages`, and `/bookings` |
| Admin | `/api/v1/admin/users`, `/vendors`, `/halls`, `/catering-packages`, `/decoration-packages`, `/bookings`, and `/payments` |

Vendor registration creates a `PENDING` vendor profile. An admin must approve it before the vendor can create or publish venues and packages. New listings start `INACTIVE`; approved vendors can activate them. Public search returns only active listings from approved vendors.

## Booking and payment behavior

Customers can book active venues. The API checks guest capacity and conflicting `PENDING` or `CONFIRMED` reservations. The hall price is calculated from the requested duration; optional catering is priced per guest and decoration is added at the listed package price. The booked rates are stored in the existing booking detail tables.

Payment rows can be created with the `DEMO` method. No live UPI, card, or net-banking gateway is configured, so those methods are rejected. An admin can reconcile a pending demo payment as `SUCCESS` or `FAILED`; successful payment confirms the booking. This is not payment processing.
