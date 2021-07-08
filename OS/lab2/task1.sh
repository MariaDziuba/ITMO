#!/bin/bash

ps | wc -l
ps | awk -F " " '{ print $1 " " $4 }' 
