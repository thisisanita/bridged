INSERT INTO "user" (
    username,
    password_hash,
    role_id,
    user_status,
    created_at,
    updated_at
)
VALUES
(
    'customer1',
    'MVP_NO_AUTH',
    (SELECT user_role_id FROM user_role WHERE user_role = 'CUSTOMER'),
    (SELECT user_status_id FROM user_status WHERE user_status = 'ACTIVE'),
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    'customer2',
    'MVP_NO_AUTH',
    (SELECT user_role_id FROM user_role WHERE user_role = 'CUSTOMER'),
    (SELECT user_status_id FROM user_status WHERE user_status = 'ACTIVE'),
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    'agent1',
    'MVP_NO_AUTH',
    (SELECT user_role_id FROM user_role WHERE user_role = 'AGENT'),
    (SELECT user_status_id FROM user_status WHERE user_status = 'ACTIVE'),
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    'agent2',
    'MVP_NO_AUTH',
    (SELECT user_role_id FROM user_role WHERE user_role = 'AGENT'),
    (SELECT user_status_id FROM user_status WHERE user_status = 'ACTIVE'),
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    'agent3',
    'MVP_NO_AUTH',
    (SELECT user_role_id FROM user_role WHERE user_role = 'AGENT'),
    (SELECT user_status_id FROM user_status WHERE user_status = 'ACTIVE'),
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);