# SpringTodoList

A learning REST API for a to-do list, built with Spring Boot. It demonstrates registration, Bearer-token login, and CRUD for tasks owned by a user.

## Stack

- Java 21, Spring Boot 4.1
- Spring Web MVC, Spring Security, Spring Data JPA, Validation
- PostgreSQL
- Maven (wrapper), Lombok

## Getting started

1. Create the database:
   ```sql
   create database todo_db;
   ```
2. Set DB credentials in `src/main/resources/application.properties`, or via the `DB_USERNAME` and `DB_PASSWORD` environment variables.
3. Run:
   ```bash
   ./mvnw spring-boot:run
   ```
4. On startup, `data.sql` creates the tables (`spring.sql.init.mode=always`).
5. Add the default role, otherwise registration fails:
   ```sql
   insert into roles (name) values ('ROLE_USER');
   ```

The app runs at `http://localhost:8080`.

## Project structure

| Package | Purpose |
|---|---|
| `controllers` | REST endpoints (`AuthController`, `TodoController`) |
| `services` | Task logic (`TodoService`, `UserDetailsServiceImpl`) |
| `security` | Security config, tokens, filter |
| `models` | JPA entities: `User`, `Role`, `Task`, `AuthToken` |
| `repositories` | Spring Data repositories |
| `dtos` | Request and response objects |
| `exceptions` | Custom exceptions and `GlobalExceptionHandler` |

## How authentication works

1. `POST /api/auth/register` or `/api/auth/login` returns `{"token": "..."}`.
2. The token is random. Only its hash is stored in the DB (`auth_tokens`). The default lifetime is 24 hours (`auth.token.ttl`).
3. The client sends the header `Authorization: Bearer <token>`.
4. `TokenAuthenticationFilter` looks up the user by token and puts them into the `SecurityContext`.
5. `POST /api/auth/logout` revokes the token.
6. There are no sessions (`STATELESS`), and CSRF is disabled.
7. Passwords are stored as BCrypt hashes.

## API

| Method | URL | Description | Access |
|---|---|---|---|
| POST | `/api/auth/register` | Register | public |
| POST | `/api/auth/login` | Log in | public |
| POST | `/api/auth/logout` | Log out | token |
| GET | `/api/todo?page=0&size=10` | List tasks (paginated) | token |
| GET | `/api/todo/{id}` | Get one task | token |
| POST | `/api/todo` | Create a task | token |
| PUT | `/api/todo/{id}` | Update a task | token |
| DELETE | `/api/todo/{id}` | Delete a task | token |

Register body:
```json
{"username": "alex", "password": "12345", "email": "alex@mail.com"}
```

Login body:
```json
{"username": "alex", "password": "12345"}
```

Task body:
```json
{"title": "Buy milk", "description": "2 liters"}
```

## Example

```bash
BASE=http://localhost:8080

TOKEN=$(curl -s -X POST $BASE/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"alex","password":"12345"}' | sed -E 's/.*"token":"([^"]+)".*/\1/')

curl -X POST $BASE/api/todo \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"title":"Buy milk","description":"2 liters"}'

curl $BASE/api/todo -H "Authorization: Bearer $TOKEN"
```

## Status codes

| Code | When |
|---|---|
| 200 | Success |
| 201 | Created |
| 204 | Deleted |
| 400 | Validation error |
| 401 | Missing, invalid, or expired token |
| 404 | Task not found or owned by another user |
