#!/bin/bash
[ ! -d /home/chenwei/tmp/mongoback  ] && mkdir -p /home/chenwei/tmp/mongoback
mongodump --host 10.20.100.238 --port 27017 --db amc_sso --out /home/chenwei/tmp/mongoback/mongodump-amc_sso-`date +%Y-%m-%d`
cd /home/chenwei/tmp/mongoback/
tar zcvf mongodump-`date +%Y-%m-%d`.tar.gz *-`date +%Y-%m-%d`
