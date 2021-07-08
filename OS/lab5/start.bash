#!/bin/bash
read N K
k=0
while [[ "$k" -ne "$K" ]]
do
./newmem "$N" &
sleep 1
k=$(echo "$k" 1 | awk '{print $1 + $2}')
done
