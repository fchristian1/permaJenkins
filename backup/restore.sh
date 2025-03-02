#!/bin/bash

# Speicherort des letzten Backups
BACKUP_DIR="/backup/jenkins"
LATEST_BACKUP=$(ls -t $BACKUP_DIR | head -n 1)
BACKUP_FILE="$BACKUP_DIR/$LATEST_BACKUP"

if [ -f "$BACKUP_FILE" ]; then
    echo "Wiederherstellen aus: $BACKUP_FILE"
    docker stop jenkins
    tar -xzvf "$BACKUP_FILE" -C /var/lib/docker/volumes/jenkins_home/_data
    docker start jenkins
    echo "Wiederherstellung abgeschlossen!"
else
    echo "Kein Backup gefunden!"
fi
