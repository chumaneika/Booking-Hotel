# catalog-service

Сервис каталога отелей и типов номеров.

## Назначение

Сервис управляет отелями, адресами, рейтингами, статусами отелей и типами номеров. Он предоставляет API для просмотра каталога, поиска отелей и административного управления отелями и комнатами.

## Порт

```text
8082
```

## База данных

```text
catalogs_db
```

Локальный URL по умолчанию:

```text
jdbc:postgresql://localhost:5432/catalogs_db
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

## API отелей

Базовый путь:

```text
/api/hotels
```

| Метод | Путь | Описание |
| --- | --- | --- |
| `GET` | `/get-all-hotels?page=0&size=10` | список всех отелей |
| `GET` | `/{id}` | детали отеля |
| `POST` | `/add-hotel` | создать отель |
| `PUT` | `/{id}` | обновить отель |
| `DELETE` | `/{id}` | деактивировать отель |
| `GET` | `/search?name=hotel&page=0&size=10` | поиск по названию |
| `GET` | `/status/{status}` | поиск по статусу |
| `GET` | `/city/{city}` | поиск по городу |
| `GET` | `/country/{country}` | поиск по стране |
| `GET` | `/rating?minRating=1&maxRating=5` | поиск по рейтингу |
| `GET` | `/search/advanced` | комбинированный поиск |
| `GET` | `/active?page=0&size=10` | активные отели |

Статусы отелей:

```text
ACTIVE, INACTIVE, CLOSED
```

## API типов номеров

Базовый путь:

```text
/api/room-types
```

| Метод | Путь | Описание |
| --- | --- | --- |
| `GET` | `?hotelId=1` | список типов номеров отеля |
| `GET` | `/{id}?hotelId=1` | детали типа номера |
| `POST` | `?hotelId=1` | создать тип номера |
| `PUT` | `/{id}?hotelId=1` | обновить тип номера |
| `DELETE` | `/{id}?hotelId=1` | удалить тип номера |

Типы кроватей:

```text
SINGLE, DOUBLE, QUEEN, KING, TWIN
```

Названия типов номеров:

```text
STANDARD, DELUXE, SUITE
```

## Безопасность

Все запросы требуют JWT.

Создание, обновление и удаление отелей и типов номеров требуют одну из ролей:

```text
ADMIN, HOTEL_OWNER, MANAGER
```

Чтение каталога требует валидный JWT.

## Переменные окружения

| Переменная | Значение по умолчанию | Описание |
| --- | --- | --- |
| `JWT_SECRET` | dev-secret из `application.yaml` | секрет подписи JWT |
| `JWT_EXPIRATION` | `86400000` | срок действия токена |
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/catalogs_db` | URL PostgreSQL |
| `SPRING_DATASOURCE_USERNAME` | `malik` | пользователь БД |
| `SPRING_DATASOURCE_PASSWORD` | `12345678` | пароль БД |

## Локальный запуск

Создайте базу `catalogs_db`, затем:

```bash
./mvnw spring-boot:run
```

## Docker

Из корня репозитория:

```bash
docker compose up --build catalog-service
```

## Сборка и тесты

```bash
./mvnw clean package
./mvnw test
```
