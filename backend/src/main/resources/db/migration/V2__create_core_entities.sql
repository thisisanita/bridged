-- ==========================================
-- PROFICIENCY
-- ==========================================
CREATE TABLE proficiency (
    proficiency_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    proficiency_level INT NOT NULL UNIQUE,
    proficiency VARCHAR(30) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


-- ==========================================
-- PRIORITY
-- ==========================================
CREATE TABLE priority (
    priority_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    priority_level INT NOT NULL UNIQUE,
    priority VARCHAR(30) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


-- ==========================================
-- CHAT STATUS
-- ==========================================
CREATE TABLE chat_status (
    chat_status_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    status VARCHAR(30) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


-- ==========================================
-- SENDER TYPE
-- ==========================================
CREATE TABLE sender_type (
    sender_type_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    sender VARCHAR(30) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


-- ==========================================
-- USER
-- ==========================================
CREATE TABLE "user" (
    user_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role_id BIGINT NOT NULL,
    user_status BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT fk_user_role
        FOREIGN KEY (role_id)
        REFERENCES user_role(user_role_id),

    CONSTRAINT fk_user_status
        FOREIGN KEY (user_status)
        REFERENCES user_status(user_status_id)
);


-- ==========================================
-- CUSTOMER
-- ==========================================
CREATE TABLE customer (
    customer_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(30) NOT NULL,
    preferred_language_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT fk_customer_user
        FOREIGN KEY (user_id)
        REFERENCES "user"(user_id),

    CONSTRAINT fk_customer_language
        FOREIGN KEY (preferred_language_id)
        REFERENCES language(language_id)
);


-- ==========================================
-- AGENT
-- ==========================================
CREATE TABLE agent (
    agent_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(30) NOT NULL,
    availability_status_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT fk_agent_user
        FOREIGN KEY (user_id)
        REFERENCES "user"(user_id),

    CONSTRAINT fk_agent_availability
        FOREIGN KEY (availability_status_id)
        REFERENCES availability_status(availability_id)
);


-- ==========================================
-- SKILL
-- ==========================================
CREATE TABLE skill (
    skill_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    skill_name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    priority_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT fk_skill_priority
        FOREIGN KEY (priority_id)
        REFERENCES priority(priority_id)
);


-- ==========================================
-- AGENT SKILL
-- ==========================================
CREATE TABLE agent_skill (
    agent_id BIGINT NOT NULL,
    skill_id BIGINT NOT NULL,
    proficiency_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,

    PRIMARY KEY (agent_id, skill_id),

    CONSTRAINT fk_agent_skill_agent
        FOREIGN KEY (agent_id)
        REFERENCES agent(agent_id),

    CONSTRAINT fk_agent_skill_skill
        FOREIGN KEY (skill_id)
        REFERENCES skill(skill_id),

    CONSTRAINT fk_agent_skill_proficiency
        FOREIGN KEY (proficiency_id)
        REFERENCES proficiency(proficiency_id)
);


-- ==========================================
-- AGENT LANGUAGE
-- ==========================================
CREATE TABLE agent_language (
    agent_id BIGINT NOT NULL,
    language_id BIGINT NOT NULL,
    proficiency_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,

    PRIMARY KEY (agent_id, language_id),

    CONSTRAINT fk_agent_language_agent
        FOREIGN KEY (agent_id)
        REFERENCES agent(agent_id),

    CONSTRAINT fk_agent_language_language
        FOREIGN KEY (language_id)
        REFERENCES language(language_id),

    CONSTRAINT fk_agent_language_proficiency
        FOREIGN KEY (proficiency_id)
        REFERENCES proficiency(proficiency_id)
);


-- ==========================================
-- CHAT SESSION
-- ==========================================
CREATE TABLE chat_session (
    chat_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    assigned_agent_id BIGINT,
    topic_skill_id BIGINT,
    preferred_language_id BIGINT,
    chat_status_id BIGINT NOT NULL,
    priority BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    triage_completed_at TIMESTAMP,
    assigned_at TIMESTAMP,
    accepted_at TIMESTAMP,
    closed_at TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT fk_chat_customer
        FOREIGN KEY (customer_id)
        REFERENCES customer(customer_id),

    CONSTRAINT fk_chat_agent
        FOREIGN KEY (assigned_agent_id)
        REFERENCES agent(agent_id),

    CONSTRAINT fk_chat_skill
        FOREIGN KEY (topic_skill_id)
        REFERENCES skill(skill_id),

    CONSTRAINT fk_chat_language
        FOREIGN KEY (preferred_language_id)
        REFERENCES language(language_id),

    CONSTRAINT fk_chat_status
        FOREIGN KEY (chat_status_id)
        REFERENCES chat_status(chat_status_id),

    CONSTRAINT fk_chat_priority
        FOREIGN KEY (priority)
        REFERENCES priority(priority_id)
);


-- ==========================================
-- CHAT MESSAGE
-- ==========================================
CREATE TABLE chat_message (
    message_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    chat_id BIGINT NOT NULL,
    sender_type_id BIGINT NOT NULL,
    sender_user_id BIGINT,
    message_type VARCHAR(30) NOT NULL DEFAULT 'TEXT',
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    accepted_at TIMESTAMP,

    CONSTRAINT fk_message_chat
        FOREIGN KEY (chat_id)
        REFERENCES chat_session(chat_id),

    CONSTRAINT fk_message_sender_type
        FOREIGN KEY (sender_type_id)
        REFERENCES sender_type(sender_type_id),

    CONSTRAINT fk_message_sender_user
        FOREIGN KEY (sender_user_id)
        REFERENCES "user"(user_id)
);