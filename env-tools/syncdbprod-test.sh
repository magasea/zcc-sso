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
