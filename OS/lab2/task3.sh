#!/bin/bash
 
pid=$$
ps -N --pid "$pid" --ppid "$pid" -o pid,start | tail -n 2 | tail -n -1
