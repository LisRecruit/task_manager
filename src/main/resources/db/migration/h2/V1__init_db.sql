CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE,
    password VARCHAR(255),
    enabled BOOLEAN,
    direct_manager_id BIGINT,
    CONSTRAINT fk_direct_manager
        FOREIGN KEY (direct_manager_id) REFERENCES users(id)
);

CREATE TABLE users_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

INSERT INTO roles (name) VALUES
    ('ROLE_ADMIN'),
    ('ROLE_USER'),
    ('ROLE_NOTAPPROVED');

INSERT INTO users (name, password, enabled) VALUES
    ('admin', '$2a$10$l0iu8HH44FTisFOW.MQXKukVc2gf81VgdYtwF5qgiMRO1CtOXcVRG', TRUE),
    ('test_user_1', '$2a$10$2y4GI0seRgCcZcWahFCG1OTegfCyXpmZB12N84/E8vRSpY6QoahMm', TRUE),
    ('test_user_2', '$2a$10$tAKtbNNP5qdmrkjg.I3/5u9cJmd0LHn6tZmAlOwaJHTTwR4fbzWbu', TRUE),
    ('test_user_3', '$2a$10$InpcmIIHi1L/SqPz5zTmFenu7j6ewHpRgvho5DGWLfhwAJNPHwuRe', TRUE),
    ('test_user_4', '$2a$10$ec7D5dpp2QmKsjJp9GdjxuTdwAIxYYmxPW4rtm/DIfAvTQmhCB6Ga', TRUE),
    ('test_user_5', '$2a$10$4JP9Ycf0nc63hlXCmgpirOStTZ7VzaJLvG7OuCKg8zrFORWl0mTri', TRUE);


INSERT INTO users_roles (user_id, role_id) VALUES
    (1, 1), -- admin - ROLE_ADMIN
    (1, 2), -- admin - ROLE_USER
    (2, 2), -- test user # - ROLE_USER
    (3, 2),
    (4, 2),
    (5, 2),
    (6, 2);

UPDATE users SET direct_manager_id = 2 WHERE id IN (3, 4);
UPDATE users SET direct_manager_id = 3 WHERE id = 5;
UPDATE users SET direct_manager_id = 4 WHERE id = 6;


CREATE TYPE task_type AS ENUM (
    'ONE_TIME',
    'POST_BILL',
    'POST_PAYMENT',
    'MATCH_BANK',
    'RECONCILE_BANK',
    'POST_JOURNAL'
);

CREATE TYPE repeatable_type AS ENUM (
    'DAILY',
    'WEEKLY',
    'BI_WEEKLY',
    'MONTHLY'
);

CREATE TABLE tasks (
    id BIGSERIAL PRIMARY KEY,
    task_description VARCHAR(255) NOT NULL,
    responsible_person_note VARCHAR(255),
    direct_manager_note VARCHAR(255),
    responsible_user_id BIGINT NOT NULL,
    task_set_by_user_id BIGINT NOT NULL,
    due_date DATE NOT NULL,
    completion_date DATE,
    period DATE NOT NULL,
    days_overdue INTEGER,
    task_complete BOOLEAN NOT NULL DEFAULT(FALSE),
    task_type task_type NOT NULL,
    repeatable BOOLEAN NOT NULL DEFAULT(FALSE),
    repeatable_type repeatable_type,
    parent_task_id BIGINT,

    CONSTRAINT fk_responsible_user FOREIGN KEY (responsible_user_id) REFERENCES users(id) ON DELETE CASCADE,

    CONSTRAINT fk_task_set_by_user FOREIGN KEY (task_set_by_user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_parent_task FOREIGN KEY (parent_task_id) REFERENCES tasks(id) ON DELETE CASCADE
);

--CREATE TABLE sub_tasks (
--    task_id BIGINT NOT NULL,
--    depends_on_task_id BIGINT NOT NULL,
--    PRIMARY KEY (task_id, depends_on_task_id),
--    CONSTRAINT fk_subtask_task FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE,
--    CONSTRAINT fk_subtask_dependency FOREIGN KEY (depends_on_task_id) REFERENCES tasks(id) ON DELETE CASCADE
--);

INSERT INTO tasks (task_description, responsible_person_note, direct_manager_note,
                   responsible_user_id, task_set_by_user_id,
                   due_date, period, task_complete, task_type, repeatable, repeatable_type, parent_task_id)
VALUES
('Reconcile bank accounts for January', '', '', 2, 1, '2025-02-20', '2025-02-01', FALSE, 'RECONCILE_BANK', FALSE, NULL, NULL),

('Match payments with invoices', '', '', 2, 1, '2025-02-22', '2025-02-01', FALSE, 'MATCH_BANK', TRUE, 'WEEKLY', NULL),

('Post vendor bill for electricity', '', '', 3, 2, '2025-02-18', '2025-02-01', FALSE, 'POST_BILL', FALSE, NULL, NULL),

('Post journal entry for payroll accrual', '', '', 3, 2, '2025-02-25', '2025-02-01', FALSE, 'POST_JOURNAL', FALSE, NULL, NULL),

('Post payment to vendor: Telco Corp', '', '', 4, 2, '2025-02-19', '2025-02-01', FALSE, 'POST_PAYMENT', FALSE, NULL, 3),

('Match bank transactions for last week', '', '', 4, 2, '2025-02-21', '2025-01-01', FALSE, 'MATCH_BANK', TRUE, 'WEEKLY', 3),

('Prepare monthly revenue report', '', '', 5, 3, '2025-02-28', '2025-01-01', FALSE, 'ONE_TIME', FALSE, NULL, NULL),

('Reconcile credit card transactions', '', '', 5, 3, '2025-02-23', '2025-01-01', FALSE, 'RECONCILE_BANK', FALSE, NULL, NULL),

('Post bill: insurance premium', '', '', 6, 4, '2025-02-15', '2025-01-01', FALSE, 'POST_BILL', FALSE, NULL, NULL),

('Prepare payment for insurance premium', '', '', 6, 4, '2025-02-17', '2025-01-01', FALSE, 'POST_PAYMENT', FALSE, NULL, NULL);

--INSERT INTO sub_tasks (task_id, depends_on_task_id) VALUES
--  (6, 3),
--  (6, 4),
--  (8, 7),
--  (10, 9);




