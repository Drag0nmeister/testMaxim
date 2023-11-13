CREATE TABLE tasks
(
    id          BIGSERIAL PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    description TEXT,
    date        DATE,
    completed   BOOLEAN      NOT NULL DEFAULT false
);
