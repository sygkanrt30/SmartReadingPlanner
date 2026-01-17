CREATE TABLE users_core (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(30) NOT NULL,
    last_name VARCHAR(30) NOT NULL,
    birth_date DATE NOT NULL,

    CONSTRAINT chk_birth_date CHECK (birth_date <= CURRENT_DATE)
);

CREATE TABLE users_credentials (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users_core(id) ON DELETE CASCADE,
    username VARCHAR(30) NOT NULL UNIQUE,
    telegram_username VARCHAR(30) NOT NULL UNIQUE,
    password VARCHAR(150) NOT NULL,
    email VARCHAR(50) NOT NULL UNIQUE,
    role VARCHAR(10) NOT NULL DEFAULT 'USER',
    email_verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_username_length CHECK (LENGTH(username) >= 3)
);

CREATE TABLE reading_profiles (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    reading_speed INTEGER DEFAULT 200,
    daily_goal INTEGER DEFAULT 30,
    timezone VARCHAR(40) DEFAULT 'Europe/Moscow',
    preferred_genres TEXT[] DEFAULT '{}',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_reading_profiles_user
        FOREIGN KEY (user_id)
        REFERENCES users_core(id)
        ON DELETE CASCADE,
    CONSTRAINT chk_reading_speed CHECK (reading_speed BETWEEN 50 AND 600),
    CONSTRAINT chk_daily_goal CHECK (daily_goal BETWEEN 5 AND 480)
);

CREATE OR REPLACE FUNCTION validate_timezone()
RETURNS TRIGGER AS $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_timezone_names
        WHERE name = NEW.timezone
    ) THEN
        RAISE EXCEPTION 'Invalid timezone: %', NEW.timezone;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_validate_timezone
BEFORE INSERT OR UPDATE ON reading_profiles
FOR EACH ROW EXECUTE FUNCTION validate_timezone();


CREATE TABLE user_settings (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    email_notifications BOOLEAN DEFAULT TRUE,
    telegram_notifications BOOLEAN DEFAULT TRUE,
    weekly_report BOOLEAN DEFAULT TRUE,
    reading_reminders BOOLEAN DEFAULT TRUE,
    privacy_level VARCHAR(20) DEFAULT 'PRIVATE',
    language VARCHAR(10) DEFAULT 'en',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_user_settings_user
        FOREIGN KEY (user_id)
        REFERENCES users_core(id)
        ON DELETE CASCADE,
    CONSTRAINT chk_privacy_level CHECK (privacy_level IN ('PUBLIC', 'PRIVATE'))
);

CREATE INDEX idx_users_email ON users_credentials(email);
CREATE INDEX idx_users_core_id ON users_core(id);
CREATE INDEX idx_users_username ON users_credentials(username);
CREATE INDEX idx_users_created_at ON users_credentials(created_at);

CREATE INDEX idx_reading_profiles_user_id ON reading_profiles(user_id);
CREATE INDEX idx_reading_profiles_genres ON reading_profiles USING GIN(preferred_genres);