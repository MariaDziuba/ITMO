#!/bin/bash

DIR=""
NEW=""
SRC_DIR="$HOME/source"
DATE=$(date +"%Y-%m-%d")
REPORT="$HOME/backup-report"
LOG_NEW=""
LOG_REP=""
DATE_SEC=$(date +"%s" -d"$DATE")

if [ ! -d $SRC_DIR ]
then
	echo "No source directory. Expected to exist: $SRC_DIR"
	exit 0
fi

for SAVED in $(ls -d -1 "$HOME/Backup-"????"-"??"-"?? 2> /dev/null)
do
	SAVED_DATE=$(grep -o "[[:digit:]]\{4\}-[[:digit:]]\{2\}-[[:digit:]]\{2\}" <<< "$SAVED")

	SAVED_DATE_SEC=$(date +"%s" -d"$SAVED_DATE")

	if (( "$DATE_SEC" - "$SAVED_DATE_SEC" < 60 * 60 * 24 * 7 ))
	then
		DIR="$SAVED"
		NEW=0
		break
	fi
done

if [[ "$DIR" == "" ]]
then
	DIR="$HOME/Backup-$DATE"
	NEW=1
	mkdir "$DIR"
fi

for FILE in $(ls $SRC_DIR)
do
	if [ -f "$DIR/$FILE" ]
	then
		if (( $(stat -c%s "$SRC_DIR/$FILE") != $(stat -c%s "$DIR/$FILE") ))
		then
			mv "$DIR/$FILE" "$DIR/$FILE.$DATE"
			LOG_REP+="\t$FILE -> $FILE.$DATE\n"
		fi
	else
		LOG_NEW+="\t$FILE\n"
	fi
	cp "$SRC_DIR/$FILE" "$DIR"
done

echo -e "NEW RECORD" >> "$REPORT"

if [[ "$NEW" == "0" ]]
then
        echo -e "$(date): Backuped in existing directory $DIR\n" >> "$REPORT"
else
        echo -e "$(date): Backuped in new directory $DIR\n" >> "$REPORT"
fi
if [[ "$LOG_NEW" != "" ]]
then
	echo -e "New files:\n$LOG_NEW" >> "$REPORT"
fi
if [[ "$LOG_REP" != "" ]]
then
	echo -e "Replaced files:\n$LOG_REP" >> "$REPORT"
fi

echo -e "\n" >> "$REPORT"
