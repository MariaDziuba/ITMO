#!/bin/bash
log=~/.local/share/xorg/Xorg.0.log
awk '$3 == "(WW)" {print}' $log | sed 's/(WW)/Warning:/' > full.log
awk '$3 == "(II)" {print}' $log | sed 's/(II)/Information:/' >> full.log
