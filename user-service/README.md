# user-service

Сервис управления пользовательскими профилями Booking Hotel.

## Назначение

Сервис хранит профиль пользователя: имя, фамилию, дату рождения и статус. Создание аккаунта и выдача токенов находятся в `auth-service`; этот сервис отвечает именно за профильные данные.

## Порт

```text
8081
```

## База данных

```text
users_db
```

Локальный URL по умолчанию:

```text
jdbc:postgresql://localhost:5432/users_db
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
/api/users
```

| Метод | Путь | Описание |
| --- | --- | --- |
| `POST` | `/create-user` | создать профиль пользователя |
| `DELETE` | `/delete/{id}` | удалить пользователя |
| `PATCH` | `/update-name` | обновить персональные данные |
| `PATCH` | `/update-status` | обновить статус пользователя |
| `GET` | `/find/{id}` | найти пользователя по UUID |
| `GET` | `/check-activity/{id}` | проверить, активен ли пользователь |
| `GET` | `/api/users?status=ACTIVE&page=0&size=20` | получить страницу пользователей по статусу |

Статусы:

```text
ACTIVE, BLOCKED, DELETED
```

### Создание профиля

```http
POST /api/users/create-user
Authorization: Bearer <access-token>
Content-Type: application/json
```

```json
{
  "firstname": "Malik",
  "surname": "Ivanov",
  "birthday": "1995-05-20"
}
```

### Обновление персональных данных

```json
{
  "id": "00000000-0000-0000-0000-000000000000",
  "firstname": "Malik",
  "surname": "Petrov",
  "birthday": "1995-05-20"
}
```

### Обновление статуса

```json
{
  "id": "00000000-0000-0000-0000-000000000000",
  "status": "BLOCKED"
}
```

## Безопасность

Все запросы требуют JWT.

Дополнительные ограничения:

- `GET /api/users`, `GET /api/users/find/**`, `GET /api/users/check-activity/**` требуют роль `ADMIN`
- `PATCH /api/users/update-name` требует роль `ADMIN`
- `PATCH /api/users/update-status` требует роль `ADMIN`
- `DELETE /api/users/delete/**` требует роль `ADMIN`
- `POST /api/users/create-user` требует любой валидный JWT

## Переменные окружения

| Переменная | Значение по умолчанию | Описание |
| --- | --- | --- |
| `JWT_SECRET` | dev-secret из `application.yaml` | секрет подписи JWT |
| `JWT_EXPIRATION` | `86400000` | срок действия токена |
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/users_db` | URL PostgreSQL |
| `SPRING_DATASOURCE_USERNAME` | `malik` | пользователь БД |
| `SPRING_DATASOURCE_PASSWORD` | `12345678` | пароль БД |

## Локальный запуск

Создайте базу `users_db`, затем:

```bash
./mvnw spring-boot:run
```

## Docker

Из корня репозитория:

```bash
docker compose up --build user-service
```

## Сборка и тесты

```bash
./mvnw clean package
./mvnw test
```
