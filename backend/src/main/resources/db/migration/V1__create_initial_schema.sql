-- ==========================================
-- USER ROLE
-- ==========================================
CREATE TABLE user_role (
    user_role_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_role VARCHAR(30) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO user_role (user_role)
VALUES
    ('CUSTOMER'),
    ('AGENT');

-- ==========================================
-- USER STATUS
-- ==========================================
CREATE TABLE user_status (
    user_status_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_status VARCHAR(30) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO user_status (user_status)
VALUES
    ('ACTIVE'),
    ('INACTIVE'),
    ('SUSPENDED');

-- ==========================================
-- LANGUAGE
-- ==========================================
CREATE TABLE language (
    language_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    language VARCHAR(30) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO language (language)
VALUES
    ('ENGLISH'),
    ('MANDARIN'),
    ('MALAY'),
    ('TAMIL');

-- ==========================================
-- AVAILABILITY STATUS
-- ==========================================
CREATE TABLE availability_status (
    availability_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    availability VARCHAR(30) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO availability_status (availability)
VALUES
    ('AVAILABLE'),
    ('BUSY'),
    ('OFFLINE');