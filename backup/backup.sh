#!/bin/bash

# Speicherort für das Backup
BACKUP_DIR="/backup"
JENKINS_BACKUP_DIR="$BACKUP_DIR/jenkins"
GRAFANA_BACKUP_DIR="$BACKUP_DIR/grafana"
TIMESTAMP=$(date +"%Y-%m-%d_%H-%M-%S")
JENKINS_BACKUP_FILE="$JENKINS_BACKUP_DIR/jenkins_backup_$TIMESTAMP.tar.gz"
GRAFANA_BACKUP_FILE="$GRAFANA_BACKUP_DIR/grafana_backup_$TIMESTAMP.tar.gz"

# Backup-Verzeichnis erstellen
mkdir -p "$JENKINS_BACKUP_DIR"
mkdir -p "$GRAFANA_BACKUP_DIR"

# Jenkins-Daten sichern
docker stop jenkins
docker stop grafana
tar -czvf "$JENKINS_BACKUP_FILE" -C /var/lib/docker/volumes/permajenkins_jenkins_home/_data .
tar -czvf "$JENKINS_BACKUP_FILE" -C /var/lib/docker/volumes/permajenkins_grafana/_data .
docker start jenkins
docker start grafana

# Alte Backups löschen (älter als 7 Tage)
find "$JENKINS_BACKUP_DIR" -type f -name "*.tar.gz" -mtime +7 -exec rm {} \;
find "$GRAFANA_BACKUP_DIR" -type f -name "*.tar.gz" -mtime +7 -exec rm {} \;

echo "Backup gespeichert unter: $JENKINS_BACKUP_FILE"
echo "Backup gespeichert unter: $GRAFANA_BACKUP_FILE"
echo "Backup abgeschlossen!"
