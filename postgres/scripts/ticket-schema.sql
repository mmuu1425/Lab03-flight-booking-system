\c ticket_db

CREATE TABLE IF NOT EXISTS ticket
(
    id            SERIAL PRIMARY KEY,
    ticket_uid    uuid UNIQUE NOT NULL,
    username      VARCHAR(80) NOT NULL,
    flight_number VARCHAR(20) NOT NULL,
    price         INT         NOT NULL,
    status        VARCHAR(20) NOT NULL
        CHECK (status IN ('PAID', 'CANCELED'))
);

-- 添加这些权限授予语句
GRANT USAGE ON SCHEMA public TO program;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO program;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO program;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO program;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO program;