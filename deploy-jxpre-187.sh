#!/bin/bash
ssh -p 22023 root@jxpre "ssh root@45.63.109.187 'cd /root/wchatgpt ; docker-compose pull ; docker-compose down ; docker-compose up -d'"
