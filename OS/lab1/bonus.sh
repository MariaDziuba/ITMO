#!/bin/bash
for arg in "$@" 
do
   ans=$(echo "scale=2; $arg*$RANDOM" | bc)
   echo "Processing of parameter $arg gave $ans"
done
