#!/usr/bin/env bash

# 等待的服务端口
export WAIT_PORTS="8050,8060,8070,8080"

IFS="," read -ra PORTS <<<"$WAIT_PORTS"
path=$(dirname "$0")

PIDs=()
for port in "${PORTS[@]}"; do
  "$path"/wait-for.sh -t 120 "http://localhost:$port/manage/health" -- echo "Host localhost:$port is active" &
  PIDs+=($!)
done

for pid in "${PIDs[@]}"; do
  if ! wait "${pid}"; then
    echo "Some services failed to start"
    exit 1
  fi
done

echo "All services are up and running!"