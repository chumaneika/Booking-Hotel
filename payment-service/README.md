# payment-service

Каркас сервиса платежей Booking Hotel.

## Назначение

Сервис подготовлен как отдельное Spring Boot приложение для будущей обработки платежей. Сейчас в коде есть базовая конфигурация, подключение к PostgreSQL, JWT security и тестовый endpoint `GET /test/hello`. Доменные платежные контроллеры и сущности пока не реализованы.

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

## Безопасность

Все запросы требуют JWT. В текущей конфигурации нет публичных endpoint.

## Переменные окружения

| Переменная | Значение по умолчанию | Описание |
| --- | --- | --- |
| `JWT_SECRET` | dev-secret из `application.yaml` | секрет подписи JWT |
| `JWT_EXPIRATION` | `86400000` | срок действия токена |
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/payment_db` | URL PostgreSQL |
| `SPRING_DATASOURCE_USERNAME` | `malik` | пользователь БД |
| `SPRING_DATASOURCE_PASSWORD` | `12345678` | пароль БД |

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

- модель платежа и статусы платежей
- API создания платежа по бронированию
- интеграцию с внешним платежным провайдером или mock-адаптером
- идемпотентность платежных операций
- callbacks/webhooks от провайдера
