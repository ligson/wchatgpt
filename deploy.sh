#!/bin/bash
ssh root@jxpre "cd /data/docker/services/wchatgpt ; docker-compose pull ; docker-compose down ; docker-compose up -d"
