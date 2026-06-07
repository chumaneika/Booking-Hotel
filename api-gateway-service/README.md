# api-gateway-service

API Gateway для Booking Hotel на Spring Cloud Gateway.

## Назначение

Сервис является единой точкой входа в систему. Он использует Spring Cloud Gateway и discovery locator, поэтому в Docker-режиме может строить маршруты к сервисам, зарегистрированным в Eureka.

## Порт

```text
8080
```

## Технологии

- Java 21
- Spring Boot
- Spring Cloud Gateway
- Spring Cloud Netflix Eureka Client
- Maven
- Docker

## Конфигурация

Основной файл: `src/main/resources/application.yaml`.

```yaml
spring:
  cloud:
    gateway:
      discovery:
        locator:
          enabled: true
          lower-case-service-id: true

server:
  port: 8080

eureka:
  client:
    enabled: false
```

Локально Eureka отключена. В Docker Compose она включается:

```yaml
EUREKA_CLIENT_ENABLED: "true"
EUREKA_CLIENT_SERVICEURL_DEFAULTZONE: http://service-discovery:8761/eureka
```

## Маршрутизация

При включенной Eureka discovery locator маршруты строятся по service id. Примеры:

```text
http://localhost:8080/auth-service/api/auth/login
http://localhost:8080/user-service/api/users/find/{id}
http://localhost:8080/catalog-service/api/hotels/active
http://localhost:8080/booking-service/api/bookings/{publicId}
```

## Локальный запуск

Для полноценной работы Gateway локально нужно также запустить `service-discovery` и включить Eureka client:

```bash
EUREKA_CLIENT_ENABLED=true \
EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://localhost:8761/eureka \
./mvnw spring-boot:run
```

## Docker

Из корня репозитория:

```bash
docker compose up --build api-gateway-service
```

Обычно Gateway запускают вместе со всей системой:

```bash
docker compose up --build
```

## Сборка и тесты

```bash
./mvnw clean package
./mvnw test
```
