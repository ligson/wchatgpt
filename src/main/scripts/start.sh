#!/usr/bin/env bash
echo "开始启动wchatgpt..."
WORK_HOME=$(cd ../;pwd)

LIB_PATH=$WORK_HOME/conf
RUN_JAVA=$JAVA_HOME/bin/java

for jar in `ls $WORK_HOME/lib/*.jar`
	do LIB_PATH=$LIB_PATH:$jar
done

echo "$RUN_JAVA -Dfile.encoding=UTF-8 -classpath $LIB_PATH org.ligson.Main"
#export LD_LIBRARY_PATH='$LD_LIBRARY_PATH:$WORK_HOME/tools/lib'
nohup $RUN_JAVA -Dfile.encoding=UTF-8 -classpath $LIB_PATH org.ligson.Main >>wchatgpt.log 2>1&
echo $!>wchatgpt.pid
PID=`cat wchatgpt.pid`
echo "启动wchatgpt成功,PID:$PID"

