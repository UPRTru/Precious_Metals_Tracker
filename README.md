# 💎 Precious Metals Tracker

Микросервисное приложение для отслеживания цен на драгоценные металлы и валюты с уведомлениями по email.

> **Технологии**: Java 17, Spring Boot 3, Gradle, PostgreSQL, Apache Kafka, Docker, Thymeleaf, Chart.js  
> **Соответствует**: «Эффективное программирование» (Джошуа Блох)

---

## 🧩 Архитектура
```angular2html
┌─────────────────┐
│ Gateway │ ← Единый фронтенд + регистрация сервисов
└────────┬────────┘
│
├─▶ Kafka ──▶ User Service ──▶ PostgreSQL
│ (пользователи, email, JSON-настройки)
│
├─▶ Kafka ──▶ Metal Price Service ──▶ (API Сбербанка / эмуляция)
│
└─▶ Kafka ──▶ Currency Price Service ──▶ (API Сбербанка / эмуляция)
```

### Модули:
- **`gateway-service`** — основной сервер, админка, фронтенд
- **`user-service`** — регистрация, настройки, email-уведомления, логирование
- **`metal-price-service`** — цены на золото, серебро, платину
- **`currency-price-service`** — курсы USD, EUR, GBP, JPY
- **`shared`** — общие DTO, Kafka-топики, enum'ы

---

## 🚀 Быстрый старт

### Требования
- JDK 17+
- Docker + Docker Compose
- Yandex Mail (для отправки уведомлений)

### 1. Настройка
Создай пароль приложения в [Yandex.Mail](https://id.yandex.ru/security/app-passwords) и задай его в переменной окружения:

```bash
export YANDEX_MAIL_PASSWORD=ваш_пароль_приложения
```

### 2. Сборка
```bash
./gradlew clean bootJar
```

### 3. Запуск через Docker Compose
```bash
docker-compose up --build
```

### Сервисы будут доступны:
#### Главная: http://localhost:8080
#### Админка: http://localhost:8080/admin/users (логин: admin, пароль: admin123)
#### Swagger (металлы): http://localhost:8082/swagger-ui.html
#### Swagger (валюты): http://localhost:8083/swagger-ui.html

## 🔐 Админка
- **`Просмотр и редактирование настроек пользователей`**
- **`Ручной запуск проверки цен`**
- **`Графики цен (30 дней)`**
- **`Лог уведомлений + экспорт в CSV`**
## 🧪 Тестирование
#### Запуск unit- и интеграционных тестов (с Testcontainers):
```bash
./gradlew test
```
`Тесты используют встроенные контейнеры PostgreSQL и Kafka.`

## 📦 Структура проекта
```angular2html
precious-metals-tracker/
├── shared/                   # Общие DTO и константы
├── gateway-service/          # Основной сервер + админка
├── user-service/             # Пользователи, email, БД
├── metal-price-service/      # Цены на металлы
├── currency-price-service/   # Курсы валют
├── docker-compose.yml        # Запуск всего стека
└── ...
```

## 📬 Уведомления
`При совпадении условий (например, цена золота ≤ 6000 ₽) пользователь получает email от test@yandex.ru.`

```json
{
  "металлы": {
    "золото": { "buyBelow": 6000 }
  },
  "валюты": {
    "USD": { "sellAbove": 95 }
  }
}
```

## 🛠️ Разработка
### Локальный запуск (без Docker)
#### 1.Запусти Kafka и PostgreSQL вручную
#### 2.Задай переменные окружения:
```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/precious_db
export YANDEX_MAIL_PASSWORD=...
```
#### 3.Запусти модули по порядку:
```bash
./gradlew :user-service:bootRun
./gradlew :metal-price-service:bootRun
./gradlew :currency-price-service:bootRun
./gradlew :gateway-service:bootRun
```

## 📜 Лицензия
### MIT
```angular2html

---

## 🔄 CI/CD: GitHub Actions (`.github/workflows/ci.yml`)

```yaml
name: CI

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest

    services:
      postgres:
        image: postgres:15
        env:
          POSTGRES_DB: test_db
          POSTGRES_USER: test
          POSTGRES_PASSWORD: test
        options: >-
          --health-cmd pg_isready
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5
        ports:
          - 5432:5432

      kafka:
        image: bitnami/kafka:3.7
        ports:
          - 9092:9092
        env:
          KAFKA_CFG_ZOOKEEPER_CONNECT: localhost:2181
          KAFKA_CFG_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
          KAFKA_CFG_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT
          ALLOW_PLAINTEXT_LISTENER: "yes"

      zookeeper:
        image: bitnami/zookeeper:3.9
        ports:
          - 2181:2181
        env:
          ALLOW_ANONYMOUS_LOGIN: "yes"

    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'

      - name: Grant execute permission
        run: chmod +x gradlew

      - name: Build with Gradle
        run: ./gradlew build

      - name: Run tests
        run: ./gradlew test
        env:
          SPRING_DATASOURCE_URL: jdbc:postgresql://localhost:5432/test_db
          SPRING_DATASOURCE_USERNAME: test
          SPRING_DATASOURCE_PASSWORD: test
          SPRING_KAFKA_BOOTSTRAP-SERVERS: localhost:9092
          YANDEX_MAIL_PASSWORD: fake_password_for_tests
```