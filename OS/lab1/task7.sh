#!/bin/bash
email_list=$(egrep -i -o -h -r  '\b[A-Z0-9._%-]+@[A-Z0-9._%-]+\.[A-Z]+\b' /etc/ | sort -u)
echo $email_list | sed 's/ /, /g' > emails.lst
