#! /bin/bash

outFile="task4.log"


for pid in $(ps -e -o pid | tail -n +2)
do
	ppid=$(grep -s "PPid:" "/proc/$pid/status" | awk '{print $2}')
        sum_exec_runtime=$(grep -s "se.sum_exec_runtime" "/proc/$pid/sched" | awk '{print $3}')
	nr_switches=$(grep -s "nr_switches" "/proc/$pid/sched" | awk '{print $3}')
	art=$(echo "scale=4; $sum_exec_runtime / $nr_switches" | bc -l)
	if [[ -z $pid ]] ; then
        	continue
    	fi
	if [[ -z $ppid ]] ; then
        	ppid=0
    	fi
    	if [[ -z $art ]] ; then
        	art=0
    	fi
	echo "ProcessID=$pid : Parent_ProcessID=$ppid : Average_Running_Time=$art"
done | sort -k 2 -V > $outFile
