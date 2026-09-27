# Train Booking System

A command-line Java application for signing up, logging in, searching train routes, booking seats, viewing bookings, and cancelling bookings.

## Features

- User sign-up and login with BCrypt password hashing
- JSON-based user and train storage
- Route search by source and destination
- Seat availability display and booking
- Booking lookup and cancellation that releases the selected seat
- Input validation for invalid menu options, routes, and seat selections

## Requirements

- Java 17 installed and selected through `JAVA_HOME`

## Run the application

From the `ticketBooking` folder in PowerShell:

```powershell
.\gradlew.bat --console=plain run
```

## Example route

The included train data supports these routes:

- bangalore to jaipur
- bangalore to delhi
- jaipur to delhi

Choose option `1` to create an account, then option `4` to search a route and book an available seat. Use option `3` to view the ticket ID, and option `5` to cancel it.

## Data storage and security

Runtime data is stored in `app/data/users.json` and `app/data/trains.json`. User passwords are never stored in plaintext; only BCrypt password hashes are persisted. JSON writes use a temporary file and an atomic replacement to reduce the chance of file corruption during a save.
