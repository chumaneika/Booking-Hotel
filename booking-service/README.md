# booking-service

Сервис бронирований Booking Hotel.

## Назначение

Сервис создает и хранит бронирования, управляет статусом бронирования и составом бронирования по типам номеров. При создании и работе с номерами использует `catalog-service` через `CATALOG_SERVICE_URL`.

## Порт

```text
8083
```

## База данных

```text
booking_db
```

Локальный URL по умолчанию:

```text
jdbc:postgresql://localhost:5432/booking_db
```

## Зависимости от других сервисов

| Сервис | Зачем нужен | Настройка |
| --- | --- | --- |
| `catalog-service` | получение данных о типе номера | `CATALOG_SERVICE_URL`, по умолчанию `http://localhost:8082` |

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

## API бронирований

Базовый путь:

```text
/api/bookings
```

| Метод | Путь | Описание |
| --- | --- | --- |
| `POST` | `/api/bookings` | создать бронирование |
| `GET` | `/api/bookings/{publicId}` | получить бронирование по публичному UUID |
| `GET` | `/api/bookings` | фильтр по `userId`, `hotelId`, `status`, `checkIn`, `checkOut` |
| `PATCH` | `/api/bookings/{publicId}/status` | обновить статус |
| `DELETE` | `/api/bookings/{publicId}` | удалить бронирование |
| `GET` | `/api/bookings/user/{userId}` | бронирования пользователя |
| `GET` | `/api/bookings/hotel/{hotelId}` | бронирования отеля |
| `GET` | `/api/bookings/{publicId}/rooms` | номера внутри бронирования |

### Создание бронирования

```http
POST /api/bookings
Authorization: Bearer <access-token>
Content-Type: application/json
```

```json
{
  "userId": 1,
  "hotelId": 1,
  "checkInDate": "2026-07-01",
  "checkOutDate": "2026-07-05",
  "rooms": [
    {
      "roomTypeId": 1,
      "quantity": 1,
      "pricePerNight": 120.00,
      "nights": 4
    }
  ]
}
```

### Обновление статуса

```json
{
  "status": "CONFIRMED"
}
```

Доступные статусы:

```text
NEW, ROOM_RESERVED, PAYMENT_PENDING, PAID, CONFIRMED, CANCELLED, EXPIRED
```

## API комнат в бронировании

Базовый путь:

```text
/api/booking-rooms
```

| Метод | Путь | Описание |
| --- | --- | --- |
| `POST` | `/api/booking-rooms` | добавить тип номера в бронирование |
| `GET` | `/api/booking-rooms/{id}` | получить запись по ID |
| `PATCH` | `/api/booking-rooms/{id}` | изменить количество |
| `DELETE` | `/api/booking-rooms/{id}` | удалить запись |
| `GET` | `/api/booking-rooms/booking/{bookingPublicId}` | список комнат бронирования |

## Безопасность

Все запросы требуют JWT.

## Переменные окружения

| Переменная | Значение по умолчанию | Описание |
| --- | --- | --- |
| `JWT_SECRET` | dev-secret из `application.yaml` | секрет подписи JWT |
| `JWT_EXPIRATION` | `86400000` | срок действия токена |
| `CATALOG_SERVICE_URL` | `http://localhost:8082` | URL catalog-service |
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/booking_db` | URL PostgreSQL |
| `SPRING_DATASOURCE_USERNAME` | `malik` | пользователь БД |
| `SPRING_DATASOURCE_PASSWORD` | `12345678` | пароль БД |

## Локальный запуск

Создайте базу `booking_db`, запустите `catalog-service`, затем:

```bash
./mvnw spring-boot:run
```

## Docker

Из корня репозитория:

```bash
docker compose up --build booking-service
```

## Сборка и тесты

```bash
./mvnw clean package
./mvnw test
```
