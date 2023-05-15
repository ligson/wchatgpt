#!/bin/bash
if [ `hostname`=='ligson-mac' ] ; then
  export JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home
fi
mvn clean
rm -rf target/*
mvn package docker:build -Dmaven.test.skip=true
docker push ligson/wchatgpt:springboot-1.0-SNAPSHOT
