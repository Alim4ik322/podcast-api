# Архитектура проекта: Podcast Hosting & Streaming API

## 1. Описание проекта

**Название:** Podcast Hosting & Streaming API  

**Краткое описание:**  
Серверное API для платформы хостинга подкастов. Авторы загружают аудиоэпизоды, система формирует RSS-ленту и собирает статистику прослушиваний. Слушатели потребляют контент через API или RSS.

**Технологии:** Java 21, Spring Boot 3, PostgreSQL, Redis, MinIO/S3.

## 2. Ролевая модель и Use Cases

### Роли

| Роль              | Описание                                      |
|-------------------|-----------------------------------------------|
| **Author**        | Автор подкаста. Создаёт подкасты и эпизоды.   |
| **Listener**      | Слушатель. Просматривает и слушает контент.   |

### Use Cases

#### Author
1. Регистрация / вход в систему
2. Создание подкаста (название, описание, обложка, категория)
3. Редактирование информации о подкасте
4. Загрузка аудиофайла эпизода + метаданные (название, описание, номер)
5. Редактирование / удаление эпизода
6. Просмотр статистики прослушиваний своего подкаста/эпизодов
7. Получение RSS-ссылки на подкаст

#### Listener
1. Регистрация / вход в систему
2. Просмотр каталога подкастов (поиск, фильтр по категориям)
3. Просмотр страницы подкаста и списка эпизодов
4. Подписка / отписка от подкаста
5. Прослушивание эпизода (запрос потока / URL файла)
6. Просмотр истории своих прослушиваний

## 3. Список сущностей (Domain Model)

Минимальный набор ключевых сущностей (≥ 5):

1. **User** — пользователь системы (может быть автором и/или слушателем)
2. **Podcast** — подкаст
3. **Episode** — эпизод подкаста
4. **Category** — категория подкаста
5. **Subscription** — подписка слушателя на подкаст
6. **ListenEvent** — факт прослушивания (для статистики)

## 4. C4 Container Diagram (Level 2)

```mermaid
C4Container
    title Container Diagram - Podcast Hosting & Streaming API

    Person(author, "Author", "Автор подкаста")
    Person(listener, "Listener", "Слушатель")

    System_Boundary(c1, "Podcast Platform") {
        Container(api, "Backend API", "Java 21 + Spring Boot 3", "Обрабатывает бизнес-логику, REST API, генерация RSS")
        ContainerDb(db, "PostgreSQL", "Database", "Хранит пользователей, подкасты, эпизоды, статистику")
        Container(redis, "Redis", "Cache & Queue", "Кэш, сессии, очередь задач обработки аудио")
        Container(storage, "Object Storage", "MinIO / S3", "Хранение аудиофайлов и обложек")
    }

    System_Ext(rss_clients, "RSS Clients", "Подкаст-приложения (Apple Podcasts, Spotify и т.д.)")

    Rel(author, api, "HTTPS/JSON", "Управляет подкастами и эпизодами")
    Rel(listener, api, "HTTPS/JSON", "Слушает, подписывается")
    Rel(api, db, "JDBC / Spring Data JPA", "Чтение/запись данных")
    Rel(api, redis, "Redis Protocol", "Кэш и задачи")
    Rel(api, storage, "S3 API", "Загрузка/скачивание файлов")
    Rel(rss_clients, api, "HTTPS", "Получает RSS-ленту")
```
## 5. ER-диаграмма базы данных (3NF)

```mermaid
erDiagram
    USERS ||--o{ PODCASTS : "creates"
    USERS ||--o{ SUBSCRIPTIONS : "has"
    USERS ||--o{ LISTEN_EVENTS : "generates"
    
    CATEGORIES ||--o{ PODCASTS : "contains"
    
    PODCASTS ||--o{ EPISODES : "has"
    PODCASTS ||--o{ SUBSCRIPTIONS : "receives"
    
    EPISODES ||--o{ LISTEN_EVENTS : "logged in"

    USERS {
        uuid id PK
        string email UK
        string password_hash
        string username
        string role
        timestamp created_at
    }

    CATEGORIES {
        bigint id PK
        string name UK
        string slug UK
    }

    PODCASTS {
        uuid id PK
        uuid author_id FK
        bigint category_id FK
        string title
        string description
        string cover_url
        timestamp created_at
    }

    EPISODES {
        uuid id PK
        uuid podcast_id FK
        string title
        string description
        string audio_url
        int duration_seconds
        int episode_number
        timestamp published_at
    }

    SUBSCRIPTIONS {
        uuid id PK
        uuid user_id FK
        uuid podcast_id FK
        timestamp created_at
    }

    LISTEN_EVENTS {
        uuid id PK
        uuid user_id FK
        uuid episode_id FK
        int listened_seconds
        timestamp listened_at
    }
```
## 6. REST API Specs

| Метод | URI | Описание | Доступ (роль) | Ожидаемый код |
|---|---|---|---|---|
| **POST** | `/api/v1/auth/register` | Регистрация нового пользователя | Все | `201 Created` |
| **POST** | `/api/v1/auth/login` | Вход и получение JWT-токена | Все | `200 OK` |
| **GET** | `/api/v1/podcasts` | Получение списка подкастов (с фильтром по категории) | Все | `200 OK` |
| **POST** | `/api/v1/podcasts` | Создание нового подкаста | Author | `201 Created` |
| **GET** | `/api/v1/podcasts/{id}` | Получение информации о конкретном подкасте | Все | `200 OK` |
| **GET** | `/api/v1/podcasts/{podcastId}/episodes` | Получение списка эпизодов подкаста | Все | `200 OK` |
| **POST** | `/api/v1/podcasts/{podcastId}/episodes` | Загрузка/создание нового эпизода | Author (Владелец) | `201 Created` |
| **DELETE** | `/api/v1/episodes/{id}` | Удаление эпизода | Author (Владелец) | `204 No Content` |
| **POST** | `/api/v1/podcasts/{podcastId}/subscribe` | Подписка на подкаст | Listener | `201 Created` |
| **DELETE** | `/api/v1/podcasts/{podcastId}/subscribe` | Отписка от подкаста | Listener | `204 No Content` |