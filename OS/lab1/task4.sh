#!/bin/bash

if [[ "$PWD" = "$HOME" ]]; then
  echo "$PWD"
  exit 0
fi

echo "Error. Script isn't run from home dir."
exit 1
