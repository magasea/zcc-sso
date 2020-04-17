#!/bin/bash
# part 1

echo '#!/bin/bash' > ./commands.sh
echo 'cd /home/chenwei/tmp' >> ./commands.sh
echo 'mysqldump --compatible=ansi -u wsssoworker -pWsamc@12345 -B WS_SSO > ./wssso-`date +%Y-%m-%d`.sql' >> ./commands.sh
ssh chenwei@10.20.100.238 '[ ! -d /home/chenwei/tmp ] && mkdir -p /home/chenwei/tmp'
scp ./commands.sh chenwei@10.20.100.238:/home/chenwei/tmp/commands.sh
ssh chenwei@10.20.100.238 'chmod 755 /home/chenwei/tmp/commands.sh && /home/chenwei/tmp/commands.sh'
[ ! -d /home/chenwei/tmp/mysqlback ] && mkdir -p /home/chenwei/tmp/mysqlback
scp chenwei@10.20.100.238:/home/chenwei/tmp/wssso-`date +%Y-%m-%d`.sql /home/chenwei/tmp/mysqlback/
ssh chenwei@10.20.100.235 '[ ! -d /home/chenwei/tmp/mysqlback ] && mkdir -p /home/chenwei/tmp/mysqlback'
scp /home/chenwei/tmp/mysqlback/wssso-`date +%Y-%m-%d`.sql chenwei@10.20.100.235:/home/chenwei/tmp/mysqlback/
ssh chenwei@10.20.100.235 "cd /home/chenwei/tmp/mysqlback/ && mysql -u wsssoworker -pWsamc@12345 < ./wssso-`date +%Y-%m-%d`.sql"


echo '#!/bin/bash' > ./mongodump.sh
echo '[ ! -d /home/chenwei/tmp/mongoback  ] && mkdir -p /home/chenwei/tmp/mongoback' >> ./mongodump.sh
echo 'mongodump --host 10.20.100.238 --port 27017 --db amc_sso --out /home/chenwei/tmp/mongoback/mongodump-amc_sso-`date +%Y-%m-%d`' >> ./mongodump.sh
echo 'cd /home/chenwei/tmp/mongoback/' >> ./mongodump.sh
echo 'tar zcvf mongodump-`date +%Y-%m-%d`.tar.gz *-`date +%Y-%m-%d`' >> ./mongodump.sh
ssh chenwei@10.20.100.238 '[ ! -d /home/chenwei/tmp ] && mkdir -p /home/chenwei/tmp'
scp ./mongodump.sh chenwei@10.20.100.238:/home/chenwei/tmp/mongodump.sh
ssh chenwei@10.20.100.238 'chmod 755 /home/chenwei/tmp/mongodump.sh && /home/chenwei/tmp/mongodump.sh'
[ ! -d /home/chenwei/tmp/mongoback ] && mkdir -p /home/chenwei/tmp/mongoback
scp chenwei@10.20.100.238:/home/chenwei/tmp/mongoback/*-`date +%Y-%m-%d`.tar.gz /home/chenwei/tmp/mongoback/
cd /home/chenwei/tmp/mongoback && tar xzvf *-`date +%Y-%m-%d`.tar.gz
mongorestore --host 10.20.100.235 --port 27017 --drop /home/chenwei/tmp/mongoback/mongodump-amc_sso-`date +%Y-%m-%d`