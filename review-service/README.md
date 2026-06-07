# review-service

Сервис отзывов Booking Hotel.

## Назначение

Сервис хранит отзывы пользователей об отелях. При создании отзыва он обращается в `booking-service`, чтобы получить данные бронирования. Заголовок `Authorization` прокидывается во внутренний запрос к `booking-service`.

## Порт

```text
8086
```

## База данных

```text
review_db
```

Локальный URL по умолчанию:

```text
jdbc:postgresql://localhost:5432/review_db
```

## Зависимости от других сервисов

| Сервис | Зачем нужен | Настройка |
| --- | --- | --- |
| `booking-service` | проверка бронирования при работе с отзывом | `BOOKING_SERVICE_URL`, по умолчанию `http://localhost:8083` |

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
/api/reviews
```

| Метод | Путь | Описание |
| --- | --- | --- |
| `POST` | `/api/reviews` | создать отзыв |
| `PATCH` | `/api/reviews` | обновить комментарий и/или рейтинг |
| `DELETE` | `/api/reviews/{reviewId}` | удалить отзыв |

### Создание отзыва

```http
POST /api/reviews
Authorization: Bearer <access-token>
Content-Type: application/json
```

```json
{
  "userId": 1,
  "hotelId": 1,
  "bookingPublicId": "00000000-0000-0000-0000-000000000000",
  "comment": "Отличный отель",
  "rating": 5
}
```

Поле `bookingPublicId` также принимает JSON alias `bookingId`.

### Обновление отзыва

```json
{
  "id": 1,
  "comment": "Обновленный комментарий",
  "rating": 4
}
```

Рейтинг должен быть от 1 до 5. Комментарий ограничен 2000 символами.

## Безопасность

Все запросы требуют JWT.

Дополнительно в сервисе есть `ReviewAccessGuard`, который используется доменной логикой для проверки доступа к операциям с отзывами.

## Переменные окружения

| Переменная | Значение по умолчанию | Описание |
| --- | --- | --- |
| `JWT_SECRET` | dev-secret из `application.yaml` | секрет подписи JWT |
| `JWT_EXPIRATION` | `86400000` | срок действия токена |
| `BOOKING_SERVICE_URL` | `http://localhost:8083` | URL booking-service |
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/review_db` | URL PostgreSQL |
| `SPRING_DATASOURCE_USERNAME` | `malik` | пользователь БД |
| `SPRING_DATASOURCE_PASSWORD` | `12345678` | пароль БД |

## Локальный запуск

Создайте базу `review_db`, запустите `booking-service`, затем:

```bash
./mvnw spring-boot:run
```

## Docker

Из корня репозитория:

```bash
docker compose up --build review-service
```

## Сборка и тесты

```bash
./mvnw clean package
./mvnw test
```
