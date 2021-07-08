#!/bin/bash

TRASH_FOLDER=$HOME/.trash
LOG_FILE=$HOME/.trash.log
LOG_FILE2=$HOME/.trash.log0

restoreFile () {
    PATH1=$1
    FILE_NAME=$2
    ln "$TRASH_FOLDER/$FILE_NAME" $PATH1
}


FILE_NAME=$1
LINE=""
if [[ ! -f "$FILE_NAME" ]]; then
    echo "Not file"
    exit 1
fi
grep $FILE_NAME $LOG_FILE |
while read LINE; do
	FILE2RESTORE=$(echo $LINE | cut -d " " -f 1)
	FILE_IN_TRASH=$(echo $LINE | cut -d " " -f 2)
    echo "$FILE_IN_TRASH"

    echo "Restore $FILE2RESTORE ? (y/n)"
    read CUR_ANS < /dev/tty
    if [[ "$CUR_ANS" == "y" ]]; then
        PARENT_DIR=$(dirname "$FILE2RESTORE") &&
            if [[ -d "$PARENT_DIR" ]]; then
                $(restoreFile "$FILE2RESTORE" "$FILE_IN_TRASH")
            else
                $(restoreFile "$HOME/$FILE_NAME" "$FILE_IN_TRASH") &&
                    echo "Directory $PARENT_DIR not exists anymore, restoring at $HOME"
            fi &&
            rm "$TRASH_FOLDER/$FILE_IN_TRASH" && {
                grep -v "$FILE2RESTORE $FILE_IN_TRASH" $LOG_FILE > $LOG_FILE2
                echo "Restored $FILE2RESTORE"
            }
    fi
done
mv "$LOG_FILE2" "$LOG_FILE"