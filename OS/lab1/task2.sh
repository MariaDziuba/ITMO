#!/bin/bash
ans=""
while read in && [[ "$in" != "q" ]]; do
  ans="$ans$in "
done
echo "$ans"
