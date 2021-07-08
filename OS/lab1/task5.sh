#!/bin/bash
awk '$6 == "<info>" {print}' /var/log/syslog > info.log 
