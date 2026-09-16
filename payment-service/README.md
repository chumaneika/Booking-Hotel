# payment-service

Сервис платежей Booking Hotel с тестовым платёжным адаптером.

## Назначение

Получает PAYMENT_REQUESTED из payment.commands.v1, сохраняет платёж PENDING в PostgreSQL. Команда идемпотентна по paymentId. Результат SUCCEEDED/FAILED сохраняется вместе с исходящим событием в outbox и доставляется в payment.events.v1. Сообщения имеют явные eventType/payload. При временной ошибке БД обработка команды повторяется.

Реальное списание денег не реализовано: нужен выбранный провайдер, его ключи и проверяемые webhooks. DEMO_PAYMENT_ENABLED=false по умолчанию; не включайте demo в настоящем окружении.

## Порт

```text
8085
```

## База данных

```text
payment_db
```

Локальный URL по умолчанию:

```text
jdbc:postgresql://localhost:5432/payment_db
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

Текущий доступный endpoint:

| Метод | Путь | Описание |
| --- | --- | --- |
| `GET` | `/test/hello` | тестовая проверка работы сервиса |
| `GET` | `/api/payments?bookingPublicId=<UUID>` | платёж владельца брони или администратора |
| `POST` | `/api/payments/{paymentId}/demo` | только при DEMO_PAYMENT_ENABLED=true: {"outcome":"SUCCEEDED"} или {"outcome":"FAILED"}; денег не списывает |

## Безопасность

Все запросы требуют JWT. В текущей конфигурации нет публичных endpoint.

## Переменные окружения

| Переменная | Значение по умолчанию | Описание |
| --- | --- | --- |
| `JWT_SECRET` | обязательно из окружения | секрет подписи JWT, минимум 32 символа |
| `JWT_EXPIRATION` | `86400000` | срок действия токена |
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/payment_db` | URL PostgreSQL |
| `SPRING_DATASOURCE_USERNAME` | `POSTGRES_USER` / `booking_hotel` | пользователь БД |
| `SPRING_DATASOURCE_PASSWORD` | `POSTGRES_PASSWORD` из окружения | обязательный пароль БД |

## Локальный запуск

Создайте базу `payment_db`, затем:

```bash
./mvnw spring-boot:run
```

## Docker

Из корня репозитория:

```bash
docker compose up --build payment-service
```

## Сборка и тесты

```bash
./mvnw clean package
./mvnw test
```

## Что стоит добавить дальше

- интеграцию с внешним платёжным провайдером
- callbacks/webhooks от провайдера
- возвраты и сверку платежей при отмене бронирования
