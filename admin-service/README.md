# admin-service

Каркас административного сервиса Booking Hotel.

## Назначение

Сервис выделен под будущие административные операции. Сейчас в коде есть базовая Spring Boot конфигурация, PostgreSQL, JWT security и тестовый endpoint `GET /test/hello`. Доменные административные контроллеры пока не реализованы.

## Порт

```text
8087
```

## База данных

```text
admin_db
```

Локальный URL по умолчанию:

```text
jdbc:postgresql://localhost:5432/admin_db
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

Все запросы требуют JWT с ролью:

```text
ADMIN
```

## Переменные окружения

| Переменная | Значение по умолчанию | Описание |
| --- | --- | --- |
| `JWT_SECRET` | dev-secret из `application.yaml` | секрет подписи JWT |
| `JWT_EXPIRATION` | `86400000` | срок действия токена |
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/admin_db` | URL PostgreSQL |
| `SPRING_DATASOURCE_USERNAME` | `malik` | пользователь БД |
| `SPRING_DATASOURCE_PASSWORD` | `12345678` | пароль БД |

## Локальный запуск

Создайте базу `admin_db`, затем:

```bash
./mvnw spring-boot:run
```

## Docker

Из корня репозитория:

```bash
docker compose up --build admin-service
```

## Сборка и тесты

```bash
./mvnw clean package
./mvnw test
```

## Что стоит добавить дальше

- административные API для просмотра пользователей, отелей, бронирований и отзывов
- аудит административных действий
- отдельные permissions поверх роли `ADMIN`
- read-only dashboard endpoints
