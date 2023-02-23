#!/bin/bash
export JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home
mvn clean
rm -rf target/*
mvn package -Dmaven.test.skip=true
cd target
pkVersion=`ls|grep jar|sed 's/.jar//'|sed 's/wchatgpt-//'`
mv wchatgpt-${pkVersion}-wchatgpt.tar.gz wchatgpt.tar.gz
mkdir docker-tmp
tar -zxvf wchatgpt.tar.gz -C docker-tmp/
cd docker-tmp
mv wchatgpt-${pkVersion} wchatgpt
cp ../../Dockerfile .
cp ../../entrypoint.sh .
docker build -t dockerhub.yonyougov.top/public/wechatgpt:$pkVersion .



