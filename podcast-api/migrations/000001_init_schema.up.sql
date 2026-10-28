-- Создание таблицы пользователей
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    username VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'LISTENER',
    CONSTRAINT chk_user_role CHECK (role IN ('AUTHOR', 'LISTENER')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Создание таблицы категорий
CREATE TABLE IF NOT EXISTS categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    slug VARCHAR(100) NOT NULL UNIQUE
);

-- Создание таблицы подкастов
CREATE TABLE IF NOT EXISTS podcasts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    author_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    category_id BIGINT NOT NULL REFERENCES categories(id) ON DELETE RESTRICT,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    cover_url VARCHAR(512),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Создание таблицы эпизодов
CREATE TABLE IF NOT EXISTS episodes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    podcast_id UUID NOT NULL REFERENCES podcasts(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    audio_url VARCHAR(512) NOT NULL,
    duration_seconds INT NOT NULL CHECK (duration_seconds > 0),
    episode_number INT NOT NULL CHECK (episode_number > 0),
    published_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Ручное создание индексов для внешних ключей (PostgreSQL не делает это автоматически)[cite: 10]
CREATE INDEX IF NOT EXISTS idx_podcasts_author_id ON podcasts(author_id);
CREATE INDEX IF NOT EXISTS idx_podcasts_category_id ON podcasts(category_id);
CREATE INDEX IF NOT EXISTS idx_episodes_podcast_id ON episodes(podcast_id);