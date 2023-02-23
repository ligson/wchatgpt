#!/bin/bash
echo "开始停止wchatgpt..."
PID=`cat wchatgpt.pid`
kill -9 $PID
echo "已经停止wchatgpt,PID:$PID"
