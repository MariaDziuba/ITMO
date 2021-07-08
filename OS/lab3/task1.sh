#!/bin/bash

mkdir -p  ~/test && { echo "Catalog test was created successfully" >> ~/report; touch ~/test/$(date +"%F_%R"); };
website="net_nikogo.ru"
ping -c 1 -w 2 $website || echo "$(date +"%F_%R") Error pinging $website" >> ~/report
