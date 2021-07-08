#!/bin/bash
find /var/log/ -regex ".*\.log" | xargs cat | wc -l
