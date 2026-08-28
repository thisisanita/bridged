-- =========================================================
-- 1. SEED CUSTOMERS
-- =========================================================

INSERT INTO customer (
    user_id,
    full_name,
    email,
    phone,
    preferred_language_id,
    created_at,
    updated_at
)
VALUES
(
    (SELECT user_id FROM "user" WHERE username = 'customer1'),
    'Alice Tan',
    'alice@example.com',
    '91234567',
    (SELECT language_id FROM language WHERE language = 'ENGLISH'),
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    (SELECT user_id FROM "user" WHERE username = 'customer2'),
    'Wei Ming',
    'weiming@example.com',
    '92345678',
    (SELECT language_id FROM language WHERE language = 'MANDARIN'),
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);


-- =========================================================
-- 2. SEED AGENTS
-- =========================================================

INSERT INTO agent (
    user_id,
    full_name,
    email,
    phone,
    availability_status_id,
    active_chat_count,
    max_active_chats,
    created_at,
    updated_at
)
VALUES
(
    (SELECT user_id FROM "user" WHERE username = 'agent1'),
    'Sarah Lim',
    'sarah@example.com',
    '93456789',
    (SELECT availability_id
     FROM availability_status
     WHERE availability = 'AVAILABLE'),
    0,
    3,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    (SELECT user_id FROM "user" WHERE username = 'agent2'),
    'Daniel Tan',
    'daniel@example.com',
    '94567890',
    (SELECT availability_id
     FROM availability_status
     WHERE availability = 'AVAILABLE'),
    0,
    3,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    (SELECT user_id FROM "user" WHERE username = 'agent3'),
    'Mei Chen',
    'mei@example.com',
    '95678901',
    (SELECT availability_id
     FROM availability_status
     WHERE availability = 'AVAILABLE'),
    0,
    3,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);


-- =========================================================
-- 3. SEED AGENT SKILLS
-- =========================================================

INSERT INTO agent_skill (
    agent_id,
    skill_id,
    proficiency_id,
    created_at,
    updated_at
)
VALUES

-- Sarah
(
    (SELECT a.agent_id
     FROM agent a
     JOIN "user" u ON a.user_id = u.user_id
     WHERE u.username = 'agent1'),
    (SELECT skill_id FROM skill WHERE skill_name = 'ACCOUNT_ENQUIRY'),
    (SELECT proficiency_id FROM proficiency WHERE proficiency = 'ADVANCED'),
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    (SELECT a.agent_id
     FROM agent a
     JOIN "user" u ON a.user_id = u.user_id
     WHERE u.username = 'agent1'),
    (SELECT skill_id FROM skill WHERE skill_name = 'LOST_STOLEN_CARD'),
    (SELECT proficiency_id FROM proficiency WHERE proficiency = 'EXPERT'),
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    (SELECT a.agent_id
     FROM agent a
     JOIN "user" u ON a.user_id = u.user_id
     WHERE u.username = 'agent1'),
    (SELECT skill_id FROM skill WHERE skill_name = 'PAYMENT_ISSUE'),
    (SELECT proficiency_id FROM proficiency WHERE proficiency = 'ADVANCED'),
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    (SELECT a.agent_id
     FROM agent a
     JOIN "user" u ON a.user_id = u.user_id
     WHERE u.username = 'agent1'),
    (SELECT skill_id FROM skill WHERE skill_name = 'CARD_FEE_WAIVER'),
    (SELECT proficiency_id FROM proficiency WHERE proficiency = 'INTERMEDIATE'),
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),

-- Daniel
(
    (SELECT a.agent_id
     FROM agent a
     JOIN "user" u ON a.user_id = u.user_id
     WHERE u.username = 'agent2'),
    (SELECT skill_id FROM skill WHERE skill_name = 'ACCOUNT_ENQUIRY'),
    (SELECT proficiency_id FROM proficiency WHERE proficiency = 'ADVANCED'),
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    (SELECT a.agent_id
     FROM agent a
     JOIN "user" u ON a.user_id = u.user_id
     WHERE u.username = 'agent2'),
    (SELECT skill_id FROM skill WHERE skill_name = 'LOAN_ENQUIRY'),
    (SELECT proficiency_id FROM proficiency WHERE proficiency = 'EXPERT'),
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    (SELECT a.agent_id
     FROM agent a
     JOIN "user" u ON a.user_id = u.user_id
     WHERE u.username = 'agent2'),
    (SELECT skill_id FROM skill WHERE skill_name = 'ACCOUNT_ACCESS'),
    (SELECT proficiency_id FROM proficiency WHERE proficiency = 'EXPERT'),
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    (SELECT a.agent_id
     FROM agent a
     JOIN "user" u ON a.user_id = u.user_id
     WHERE u.username = 'agent2'),
    (SELECT skill_id FROM skill WHERE skill_name = 'FRAUDULENT_TRANSACTION'),
    (SELECT proficiency_id FROM proficiency WHERE proficiency = 'ADVANCED'),
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),

-- Mei
(
    (SELECT a.agent_id
     FROM agent a
     JOIN "user" u ON a.user_id = u.user_id
     WHERE u.username = 'agent3'),
    (SELECT skill_id FROM skill WHERE skill_name = 'LOAN_ENQUIRY'),
    (SELECT proficiency_id FROM proficiency WHERE proficiency = 'ADVANCED'),
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    (SELECT a.agent_id
     FROM agent a
     JOIN "user" u ON a.user_id = u.user_id
     WHERE u.username = 'agent3'),
    (SELECT skill_id FROM skill WHERE skill_name = 'PAYMENT_ISSUE'),
    (SELECT proficiency_id FROM proficiency WHERE proficiency = 'EXPERT'),
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    (SELECT a.agent_id
     FROM agent a
     JOIN "user" u ON a.user_id = u.user_id
     WHERE u.username = 'agent3'),
    (SELECT skill_id FROM skill WHERE skill_name = 'ACCOUNT_ACCESS'),
    (SELECT proficiency_id FROM proficiency WHERE proficiency = 'ADVANCED'),
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    (SELECT a.agent_id
     FROM agent a
     JOIN "user" u ON a.user_id = u.user_id
     WHERE u.username = 'agent3'),
    (SELECT skill_id FROM skill WHERE skill_name = 'FRAUDULENT_TRANSACTION'),
    (SELECT proficiency_id FROM proficiency WHERE proficiency = 'EXPERT'),
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);


-- =========================================================
-- 4. SEED AGENT LANGUAGES
-- =========================================================

INSERT INTO agent_language (
    agent_id,
    language_id,
    proficiency_id,
    created_at,
    updated_at
)
VALUES

-- Sarah
(
    (SELECT a.agent_id
     FROM agent a
     JOIN "user" u ON a.user_id = u.user_id
     WHERE u.username = 'agent1'),
    (SELECT language_id FROM language WHERE language = 'ENGLISH'),
    (SELECT proficiency_id FROM proficiency WHERE proficiency = 'EXPERT'),
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    (SELECT a.agent_id
     FROM agent a
     JOIN "user" u ON a.user_id = u.user_id
     WHERE u.username = 'agent1'),
    (SELECT language_id FROM language WHERE language = 'MANDARIN'),
    (SELECT proficiency_id FROM proficiency WHERE proficiency = 'INTERMEDIATE'),
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),

-- Daniel
(
    (SELECT a.agent_id
     FROM agent a
     JOIN "user" u ON a.user_id = u.user_id
     WHERE u.username = 'agent2'),
    (SELECT language_id FROM language WHERE language = 'ENGLISH'),
    (SELECT proficiency_id FROM proficiency WHERE proficiency = 'EXPERT'),
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    (SELECT a.agent_id
     FROM agent a
     JOIN "user" u ON a.user_id = u.user_id
     WHERE u.username = 'agent2'),
    (SELECT language_id FROM language WHERE language = 'MANDARIN'),
    (SELECT proficiency_id FROM proficiency WHERE proficiency = 'ADVANCED'),
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),

-- Mei
(
    (SELECT a.agent_id
     FROM agent a
     JOIN "user" u ON a.user_id = u.user_id
     WHERE u.username = 'agent3'),
    (SELECT language_id FROM language WHERE language = 'MANDARIN'),
    (SELECT proficiency_id FROM proficiency WHERE proficiency = 'EXPERT'),
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    (SELECT a.agent_id
     FROM agent a
     JOIN "user" u ON a.user_id = u.user_id
     WHERE u.username = 'agent3'),
    (SELECT language_id FROM language WHERE language = 'ENGLISH'),
    (SELECT proficiency_id FROM proficiency WHERE proficiency = 'ADVANCED'),
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);