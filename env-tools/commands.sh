#!/bin/bash
cd /home/chenwei/tmp
mysqldump  -u wsssoworker -pWsamc@12345 -B WS_SSO > ./wssso-`date +%Y-%m-%d`.sql
