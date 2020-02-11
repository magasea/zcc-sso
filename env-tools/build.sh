#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd)"
cd ${DIR}/..
mvn clean compile -o -DskipTests package
scp env-tools/startup.sh chenwei@10.20.100.235:/home/chenwei/working/sso
scp target/amc-sso-0.1-SNAPSHOT.jar chenwei@10.20.100.235:/home/chenwei/working/sso


ssh chenwei@10.20.100.235 "cd /home/chenwei/working/sso && ./startup.sh "

