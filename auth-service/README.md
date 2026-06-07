# auth-service

Сервис аутентификации и выпуска JWT для Booking Hotel.

## Назначение

Сервис отвечает за регистрацию аккаунтов, вход по email и паролю, выпуск access token и refresh token, а также ротацию refresh token.

## Порт

```text
8088
```

## База данных

```text
auth_db
```

По умолчанию локально используется PostgreSQL:

```text
jdbc:postgresql://localhost:5432/auth_db
```

## Технологии

- Java 17
- Spring Boot Web
- Spring Security
- Spring Data JPA
- PostgreSQL
- JWT (`jjwt`)
- Lombok
- MapStruct
- Maven
- Docker

## API

Базовый путь:

```text
/api/auth
```

| Метод | Путь | Описание |
| --- | --- | --- |
| `POST` | `/register` | регистрация нового аккаунта |
| `POST` | `/login` | вход и получение пары токенов |
| `POST` | `/refresh` | обновление access token через refresh token |

### Регистрация

```http
POST /api/auth/register
Content-Type: application/json
```

```json
{
  "firstname": "Malik",
  "surname": "Ivanov",
  "email": "malik@example.com",
  "password": "password123"
}
```

Ответ содержит `token`, `refreshToken`, `type`, `userId`, `email`, `firstname`, `surname`.

### Вход

```http
POST /api/auth/login
Content-Type: application/json
```

```json
{
  "email": "malik@example.com",
  "password": "password123"
}
```

### Refresh

```http
POST /api/auth/refresh
Content-Type: application/json
```

```json
{
  "refreshToken": "<refresh-token>"
}
```

## Безопасность

`/api/auth/**` открыт без авторизации. Остальные запросы требуют JWT.

Роли аккаунтов:

```text
USER, ADMIN, HOTEL_OWNER, MANAGER
```

## Переменные окружения

| Переменная | Значение по умолчанию | Описание |
| --- | --- | --- |
| `JWT_SECRET` | dev-secret из `application.yaml` | секрет подписи JWT |
| `JWT_EXPIRATION` | `900000` | срок access token, 15 минут |
| `JWT_REFRESH_EXPIRATION` | `604800000` | срок refresh token, 7 дней |
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/auth_db` | URL PostgreSQL |
| `SPRING_DATASOURCE_USERNAME` | `malik` | пользователь БД |
| `SPRING_DATASOURCE_PASSWORD` | `12345678` | пароль БД |

## Локальный запуск

Создайте базу `auth_db`, затем:

```bash
./mvnw spring-boot:run
```

## Docker

Из корня репозитория:

```bash
docker compose up --build auth-service
```

## Сборка и тесты

```bash
./mvnw clean package
./mvnw test
```
