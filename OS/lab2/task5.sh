#!/bin/bash

let ppid=0
let art=0
let sum=0
let cnt=1

res=""
outFile="task5.log"
inFile="task4.log"

while read cur
do :
  nextPpid=$(echo $cur | awk -F '=' '{ print $3; }' | awk '{ print $1; }')
  nextArt=$(echo $cur | awk -F '=' '{ print $4; }')
  if [ $ppid == $nextPpid ];
  then
    sum=$(echo "scale=6; $sum+$nextArt" | bc)
    let cnt=$cnt+1
  else
    art=$(echo "scale=6; $sum/$cnt" | bc)
    case $sleepAvg in .*) art=0$art;; esac
    res=$res"\nAverage Sleeping Children of ParentID="$ppid" is "$art
    ppid=$nextPpid
    sum=$nextArt
    cnt=1
  fi
  res=$res'\n'$cur
done < $inFile
sleepAvg=$(echo "scale=6; $sum/$cnt" | bc)
case $art in .*) sleepAvg=0$art;; esac
res=$res"\nAverage Sleeping Children of ParentID="$ppid" is "$art
echo -e $res > $outFile
