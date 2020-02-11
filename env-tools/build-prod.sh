#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd)"
cd ${DIR}/..
mvn clean compile -DskipTests package
scp target/amc-sso-0.1-SNAPSHOT.jar chenwei@10.20.100.238:/home/chenwei/working/sso


ssh chenwei@10.20.100.238 "cd /home/chenwei/working/sso && ./startup-prod.sh "
