#!/bin/bash

echo "ping -c 4 www.google.com" | at -m now +1 minute
