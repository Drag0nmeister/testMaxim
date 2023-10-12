CREATE TABLE tasks
(
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    title        VARCHAR(255),
    description  TEXT,
    date         DATE,
    is_completed BOOLEAN
);
