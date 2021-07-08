#!/bin/bash
man bash | grep -o "[A-Za-z0-9]\{4,\}" | sort | uniq -c | sort -r -n | head -3
