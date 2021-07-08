#!/bin/bash

while true
do
    read s
    case $s in
        "+")
            kill -USR1 $(cat task6.tmp)
            ;;
        "*")
            kill -USR2 $(cat task6.tmp)
            ;;
        TERM)
            kill -SIGTERM $(cat task6.tmp)
            exit 0
            ;;
        *)
            continue
            ;;
    esac
done
