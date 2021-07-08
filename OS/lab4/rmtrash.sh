#!/bin/bash

TRASH_FOLDER=$HOME/.trash
LOG_FILE=$HOME/.trash.log

FILE_NAME=$1

if [[ ! -f "$FILE_NAME" ]]; then
    echo "Not file"
    exit 1
fi

if [[ ! -d "$TRASH_FOLDER" ]]; then
    mkdir $TRASH_FOLDER
fi

CNT=0
TRASH_NAME="$FILE_NAME-$CNT"
while [[ -e "$TRASH_FOLDER/$TRASH_NAME" ]] ; do
    $(( CNT=CNT+1 ))
    TRASH_NAME="$FILE_NAME-$CNT"
done

ln $FILE_NAME "$TRASH_FOLDER/$TRASH_NAME"
rm "$FILE_NAME" && echo "$(realpath $FILE_NAME) $TRASH_NAME" >> "$LOG_FILE"
