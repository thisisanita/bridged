-- ==========================================
-- PROFICIENCY
-- ==========================================

INSERT INTO proficiency (proficiency_level, proficiency)
VALUES
    (1, 'BEGINNER'),
    (2, 'INTERMEDIATE'),
    (3, 'ADVANCED'),
    (4, 'EXPERT'),
    (5, 'SPECIALIST');


-- ==========================================
-- CHAT STATUS
-- ==========================================

INSERT INTO chat_status (status)
VALUES
    ('TRIAGE'),
    ('WAITING'),
    ('ASSIGNED'),
    ('ACTIVE'),
    ('CLOSED');


-- ==========================================
-- PRIORITY
-- ==========================================

INSERT INTO priority (priority_level, priority)
VALUES
    (1, 'CRITICAL'),
    (2, 'HIGH'),
    (3, 'NORMAL'),
    (4, 'LOW');


-- ==========================================
-- SENDER TYPE
-- ==========================================

INSERT INTO sender_type (sender)
VALUES
    ('AGENT'),
    ('CUSTOMER'),
    ('BOT');