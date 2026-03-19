# Fake Data Generator

Java/Spring Boot REST API that generates fake data for nonexistent Danish persons. The backend has been rewritten from PHP to Java, and the database backend has been changed from MariaDB to PostgreSQL.

## Stack

- Java 21
- Spring Boot 3
- Spring Web
- Spring JDBC
- PostgreSQL 17
- Docker Compose

## Data Sources

- Person names and genders are loaded from `src/main/resources/person-names.json`
- Postal codes and town names are loaded into PostgreSQL from `db/init/01-addresses.sql`

## Run

- Start: `docker compose up --build -d`
- Stop: `docker compose down -v`

The API is available at `http://localhost:8081`.
PostgreSQL is exposed on `localhost:5433` for local IntelliJ runs.

## Tests

The project is set up with JUnit 5 and Mockito.

- Run all tests: `mvn test`
- Run one test class: `mvn -Dtest=FakeInfoServiceTest test`

Current test layout:

- `src/test/java/dk/fakeinfo/service`: fast unit tests with mocked dependencies
- `src/test/java/dk/fakeinfo/controller`: controller unit tests without starting Spring

## API Endpoints

| Method | Endpoint |
| ------ | -------- |
| GET | `/cpr` |
| GET | `/name-gender` |
| GET | `/name-gender-dob` |
| GET | `/cpr-name-gender` |
| GET | `/cpr-name-gender-dob` |
| GET | `/address` |
| GET | `/phone` |
| GET | `/person` |
| GET | `/person?n=<number_of_fake_persons>` |

## Example Responses

`GET /cpr`

```json
{
  "CPR": "0412489054"
}
```

`GET /person`

```json
{
  "CPR": "0107832911",
  "firstName": "Michelle W.",
  "lastName": "Henriksen",
  "gender": "female",
  "birthDate": "1983-07-01",
  "address": {
    "street": "GYØVCoØMeceOjøtÆgvYrøQQDascNFCHArnSNrxub",
    "number": "521",
    "floor": 74,
    "door": "tv",
    "postal_code": "8670",
    "town_name": "Låsby"
  },
  "phoneNumber": "58676658"
}
```

`GET /person?n=3`

```json
[
  {
    "CPR": "2411576095",
    "firstName": "Laurits S.",
    "lastName": "Kjeldsen",
    "gender": "male",
    "birthDate": "1957-11-24",
    "address": {
      "street": "aÅGgøhIbJXVsRÆøjLnåæFoXtsgU Ø NINFYwBnaø",
      "number": "413",
      "floor": 46,
      "door": "tv",
      "postal_code": "8700",
      "town_name": "Horsens"
    },
    "phoneNumber": "35753186"
  }
]
```

## Project Layout

- `src/main/java/dk/fakeinfo/controller`: HTTP endpoints and API error handling
- `src/main/java/dk/fakeinfo/service`: fake person generation logic
- `src/main/java/dk/fakeinfo/repository`: PostgreSQL queries
- `src/main/resources`: application config and bundled JSON data
- `db/init`: PostgreSQL initialization scripts

## Notes

- The Java implementation preserves the existing endpoint names and response shapes so consumers do not need to change.
