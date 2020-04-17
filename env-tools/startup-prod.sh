#!/bin/bash
#export JAVA_HOME=/home/chenwei/workingTools/jdk1.8.0_191
#export JAVA_HOME=/home/chenwei/workingTools/jdk-12
export JAVA_HOME=/home/chenwei/workingTools/jdk-10.0.2
export PATH=${JAVA_HOME}/bin:${PATH}
export LANG=en_US.utf8
export JAVA_OPTS="-Dsun.jnu.encoding=UTF-8 -Dfile.encoding=UTF-8"
execJarName_sso=/home/chenwei/working/sso/amc-sso-0.1-SNAPSHOT.jar
pattern_sso=amc-sso


killProcess() {
    echo "$1"
    result=`ps -ef | grep "$1" | grep -v grep | awk '{print $2}'| wc -l`
    echo "now thre is $result process like:$1 are running"
    if [ $result -gt 0 ]; then
        echo "to kill process with pattern:$1 and pid:$result"
        kill $(ps -ef | grep "$1" | grep -v grep | awk '{print $2}')
    fi
}

killProcess ${pattern_sso}

echo ${execJarName_sso}


sleep 5
nohup java -jar -Dspring.profiles.active=preProd -XX:+UseG1GC -Xms128M -Xmx512M ${execJarName_sso} &>sso.log &
