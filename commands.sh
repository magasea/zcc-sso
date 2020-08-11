#!/bin/bash
cd /home/chenwei/tmp
mysqldump --compatible=ansi -u root -pWensheng@123 -B WS_SSO > ./wssso-`date +%Y-%m-%d`.sql
