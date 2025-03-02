#!/bin/bash

# Speicherort für das Backup
BACKUP_DIR="/backup/jenkins"
TIMESTAMP=$(date +"%Y-%m-%d_%H-%M-%S")
BACKUP_FILE="$BACKUP_DIR/jenkins_backup_$TIMESTAMP.tar.gz"

# Backup-Verzeichnis erstellen
mkdir -p "$BACKUP_DIR"

# Jenkins-Daten sichern
docker stop jenkins
tar -czvf "$BACKUP_FILE" -C /var/lib/docker/volumes/permajenkins_jenkins_home/_data .
docker start jenkins

# Alte Backups löschen (älter als 7 Tage)
find "$BACKUP_DIR" -type f -name "*.tar.gz" -mtime +7 -exec rm {} \;

echo "Backup gespeichert unter: $BACKUP_FILE"
