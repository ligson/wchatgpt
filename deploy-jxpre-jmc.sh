#!/bin/bash
ssh  -p 22023 root@jxpre "ssh root@149.28.98.249 'cd /root/wchatgpt ; docker-compose pull ; docker-compose down ; docker-compose up -d'"
