#!/bin/bash

touch before
touch after

ps a u x | awk '{if ($6 != 0) print $2" "$6" "$11}' | sort -n -k 1 | tail -n +2 | head -n -6 > before

sleep 60s

ps a u x | awk '{if ($6 != 0) print $2" "$6" "$11}' | sort -n -k 1 | tail -n +2 | head -n -6 > after

cat before | while read in
do
    pid=$(echo $in | awk '{print $1}')
    mem_b=$(echo $in | awk '{print $2}')
    cmd=$(echo $in | awk '{print $3}')
    mem_a=$(cat after | awk -v id="$pid" '{if ($1 == id) print $2}')
    diff=$(echo "$mem_a - $mem_b" | bc)
    echo $pid":"$cmd":"$diff
done | sort -t ":" -n -k 3 | head -n 3

rm after
rm before
