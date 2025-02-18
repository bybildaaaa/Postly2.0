# Postly

Postly — это простой RESTful API-сервис на Spring Boot для управления постами пользователей.

## Функциональность

- Получение всех постов
- Получение поста по id
- Поиск постов по username

## Используемые технологии

- Java 23
- Spring Boot 3
- Spring Web
- Maven

## Структура проекта
postly.example.postly

├── controllers # Контроллеры (API) 

├── services # Сервисный слой (логика приложения) 

├── models # Модели данных (Post) 

├── PostlyApplication.java # Точка входа

## API Эндпоинты

| Метод  | URL               | Описание                   | Пример запроса |
|--------|------------------|---------------------------|----------------|
| GET    | /posts         | Получить все посты        | curl -X GET http://localhost:8080/posts |
| GET    | /posts/{id}    | Получить пост по id     | curl -X GET http://localhost:8080/posts/1 |
| GET    | /posts?username={name} | Получить посты по username | curl -X GET http://localhost:8080/posts?username=Biba |

## Как запустить?

1. Убедись, что установлен JDK 17+ и Maven.
2. Склонируй репозиторий:
   ```sh
   git clone https://github.com/your-username/Postly.git
3. Перейди в папку проекта:
   cd Postly
4. Запусти приложение:
   mvn spring-boot:run
5. API будет доступно по адресу: http://localhost:8080
