-- flight-schema.sql
\c flight_db

-- 创建表
CREATE TABLE IF NOT EXISTS airport
(
    id      SERIAL PRIMARY KEY,
    name    VARCHAR(255),
    city    VARCHAR(255),
    country VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS flight
(
    id              SERIAL PRIMARY KEY,
    flight_number   VARCHAR(20)              NOT NULL,
    datetime        TIMESTAMP WITH TIME ZONE NOT NULL,
    from_airport_id INT REFERENCES airport (id),
    to_airport_id   INT REFERENCES airport (id),
    price           INT                      NOT NULL
);

-- 给用户授权（关键：在连接到 flight_db 后执行）
GRANT USAGE ON SCHEMA public TO program;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO program;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO program;

-- 插入测试数据
INSERT INTO airport (id, name, city, country) VALUES
(1, 'Шереметьево', 'Москва', 'Россия'),
(2, 'Пулково', 'Санкт-Петербург', 'Россия')
ON CONFLICT (id) DO NOTHING;

INSERT INTO flight (id, flight_number, datetime, from_airport_id, to_airport_id, price) VALUES
(1, 'AFL031', '2021-10-08 20:00:00+03', 2, 1, 1500)
ON CONFLICT (id) DO NOTHING;

-- 确保权限设置
GRANT USAGE ON SCHEMA public TO program;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO program;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO program;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO program;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO program;