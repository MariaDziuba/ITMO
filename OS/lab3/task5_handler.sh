#!/bin/bash

res=1
mode="+"

echo $$ > pipe

(tail -n 0 -f pipe) |
while true
do
    read s
    case $s in
        "+")
            mode="+"
            echo "Changed mode to the +"
            ;;
        "*")
            mode="*"
            echo "Changed mode to the *"
            ;;
        "QUIT")
            echo "Stopped by QUIT message"
            rm pipe
            exit 0
            ;;
        *)
            if [[ "$s" =~ [0-9]+ ]]
            then
                case $mode in
                    "+")
                        res=$(($res + $s))
                        echo $res
                        ;;
                    "*")
                        res=$(($res * $s))
                        echo $res
                        ;;
                esac
            else
                echo "Stopped by incorrect message"
                rm pipe
                killall tail
                exit 1
            fi
            ;;
    esac

    sleep 1
done
