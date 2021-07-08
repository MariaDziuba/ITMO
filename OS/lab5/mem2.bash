#!/bin/bash
k=0
array=()
echo "" > report2.log
while true
do
array+=(1 2 3 4 5 6 7 8 9 10)
k=$(echo "$k" 1 | awk '{print $1 + $2}')
if [[ "$k" == "1000" ]] 
then 
echo "${#array[*]}" >> report.log
k=0
fi
done

