# notification-service

Сервис уведомлений Booking Hotel.

## Назначение

Сервис создает записи уведомлений и отправляет email-уведомления. Сейчас в публичном API реализована отправка письма об успешной регистрации.

## Порт

```text
8084
```

## База данных

```text
notification_db
```

Локальный URL по умолчанию:

```text
jdbc:postgresql://localhost:5432/notification_db
```

## Технологии

- Java 17
- Spring Boot Web
- Spring Security
- Spring Data JPA
- Spring Mail
- PostgreSQL
- JWT (`jjwt`)
- Lombok
- MapStruct
- Maven
- Docker

## API

Базовый путь:

```text
/api/notifications
```

| Метод | Путь | Описание |
| --- | --- | --- |
| `POST` | `/registration-success` | отправить email об успешной регистрации |

### Уведомление об успешной регистрации

```http
POST /api/notifications/registration-success
Authorization: Bearer <access-token>
Content-Type: application/json
```

```json
{
  "userId": 1,
  "name": "Malik",
  "email": "malik@example.com"
}
```

## Типы и статусы

Типы уведомлений:

```text
EMAIL, SMS, PUSH
```

Статусы:

```text
PENDING, SEND, FAILED
```

## Безопасность

Все запросы требуют JWT.

`POST /api/notifications/**` требует одну из ролей:

```text
ADMIN, MANAGER
```

## SMTP

По умолчанию используется SMTP Gmail, но логин и пароль пустые. Для реальной отправки настройте переменные окружения:

| Переменная | Значение по умолчанию | Описание |
| --- | --- | --- |
| `MAIL_HOST` | `smtp.gmail.com` | SMTP host |
| `MAIL_PORT` | `587` | SMTP port |
| `MAIL_USERNAME` | пусто | SMTP username |
| `MAIL_PASSWORD` | пусто | SMTP password или app password |
| `MAIL_FROM` | значение `MAIL_USERNAME` | адрес отправителя |

## Остальные переменные окружения

| Переменная | Значение по умолчанию | Описание |
| --- | --- | --- |
| `JWT_SECRET` | dev-secret из `application.yaml` | секрет подписи JWT |
| `JWT_EXPIRATION` | `86400000` | срок действия токена |
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/notification_db` | URL PostgreSQL |
| `SPRING_DATASOURCE_USERNAME` | `malik` | пользователь БД |
| `SPRING_DATASOURCE_PASSWORD` | `12345678` | пароль БД |

## Локальный запуск

Создайте базу `notification_db`, настройте SMTP при необходимости, затем:

```bash
./mvnw spring-boot:run
```

## Docker

Из корня репозитория:

```bash
docker compose up --build notification-service
```

## Сборка и тесты

```bash
./mvnw clean package
./mvnw test
```
