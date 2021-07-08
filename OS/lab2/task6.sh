#!/bin/bash

all_pr=/proc/*/status

for pr in $all_pr
do
   pid=$(grep -w -m1 "Pid:" $pr)
   vmrcc=$(grep -w -m1 "VmRSS:" $pr)
   if [[ $pid != "" && $vmrcc != "" ]]
      then awk -F ":" {print $pid $vmrcc}
   fi
done | sort -n -k 4 | tail -1
