#!/bin/bash

ps ax -o pid,command | grep "\/sbin\/.*" | awk -F ':' '{print $1, $2}'
