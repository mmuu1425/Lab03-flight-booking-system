#!/usr/bin/env bash
set -e

# 设置变量
export VARIANT="1"
export SCRIPT_PATH=/docker-entrypoint-initdb.d/
export PGPASSWORD=postgres

# 等待 PostgreSQL 完全启动
until psql -U postgres -c '\q'; do
  >&2 echo "PostgreSQL is unavailable - sleeping"
  sleep 1
done

echo "=== 开始数据库初始化 ==="

# 1. 创建用户（已经在 10-create-user.sql 中完成）

# 2. 创建数据库
echo "创建数据库..."
psql -v ON_ERROR_STOP=1 -U postgres -f "$SCRIPT_PATH/scripts/db-$VARIANT.sql"

# 3. 创建表结构和授予权限
echo "初始化 flight_db..."
psql -v ON_ERROR_STOP=1 -U postgres -d flight_db -f "$SCRIPT_PATH/scripts/flight-schema.sql"

echo "初始化 bonus_db..."
psql -v ON_ERROR_STOP=1 -U postgres -d bonus_db -f "$SCRIPT_PATH/scripts/bonus-schema.sql"

echo "初始化 ticket_db..."
psql -v ON_ERROR_STOP=1 -U postgres -d ticket_db -f "$SCRIPT_PATH/scripts/ticket-schema.sql"

echo "=== 数据库初始化完成 ==="