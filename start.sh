#!/bin/bash

# 构建所有服务
echo "Building services..."
docker-compose build

# 启动服务
echo "Starting services..."
docker-compose up -d

# 等待服务健康检查
echo "Waiting for services to be healthy..."
./wait-script.sh