#!/bin/bash
chmod u+x task4_inf.sh

./task4_inf.sh&pid1=$!
./task4_inf.sh&pid2=$!
./task4_inf.sh&pid3=$!

echo $pid1 $pid2
renice +20 $pid1

sleep 3

kill $pid1
kill $pid2
kill $pid3
