# service-discovery

Eureka Server для регистрации и обнаружения микросервисов Booking Hotel.

## Назначение

Сервис запускает Spring Cloud Netflix Eureka Server и предоставляет registry, в котором остальные сервисы регистрируются при Docker-запуске. В локальных `application.yaml` клиентских сервисов Eureka отключена, но `docker-compose.yml` включает ее через переменные окружения.

## Порт

```text
8761
```

Eureka UI:

```text
http://localhost:8761
```

## Технологии

- Java 17
- Spring Boot
- Spring Cloud Netflix Eureka Server
- Maven
- Docker

## Конфигурация

Основной файл: `src/main/resources/application.yaml`.

```yaml
spring:
  application:
    name: service-discovery

server:
  port: 8761

eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
```

## Локальный запуск

```bash
./mvnw spring-boot:run
```

## Docker

Из корня репозитория:

```bash
docker compose up --build service-discovery
```

Или весь проект:

```bash
docker compose up --build
```

## Проверка

```bash
curl http://localhost:8761
```

В браузере откроется Eureka dashboard со списком зарегистрированных сервисов.

## Сборка и тесты

```bash
./mvnw clean package
./mvnw test
```
