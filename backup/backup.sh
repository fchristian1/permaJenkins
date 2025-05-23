#!/bin/bash
# check sudo für dieses script benötigt

# Prüfen, ob das Script mit sudo ausgeführt wird
if [ "$EUID" -ne 0 ]; then
    echo "Bitte führe das Script mit sudo-Rechten aus!"
    exit 1
fi

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
tar -czvf "$GRAFANA_BACKUP_FILE" -C /var/lib/docker/volumes/permajenkins_grafana_data/_data .

docker start jenkins
docker start grafana

# Alte Backups löschen (älter als 7 Tage)
find "$JENKINS_BACKUP_DIR" -type f -name "*.tar.gz" -mtime +7 -exec rm {} \;
find "$GRAFANA_BACKUP_DIR" -type f -name "*.tar.gz" -mtime +7 -exec rm {} \;

echo "Backup gespeichert unter: $JENKINS_BACKUP_FILE"
echo "Backup gespeichert unter: $GRAFANA_BACKUP_FILE"
echo "Backup abgeschlossen!"
