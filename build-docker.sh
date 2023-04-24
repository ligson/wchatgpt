#!/bin/bash
export JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home
mvn clean
rm -rf target/*
mvn package docker:build -Dmaven.test.skip=true
