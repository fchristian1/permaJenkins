#!/bin/bash

# prüfen ob das Script mit sudo ausgeführt wird
if [ "$EUID" -ne 0 ]; then
    echo "Bitte führe das Script mit sudo-Rechten aus"
    exit 1
fi

# Speicherort des letzten Backups
BACKUP_DIR="/backup"
JENKINS_BACKUP_DIR="$BACKUP_DIR/jenkins"
GRAFANA_BACKUP_DIR="$BACKUP_DIR/grafana"
JENKINS_LATEST_BACKUP=$(ls -t $JENKINS_BACKUP_DIR | head -n 1)
GRAFANA_LATEST_BACKUP=$(ls -t $GRAFANA_BACKUP_DIR | head -n 1)

JENKINS_BACKUP_FILE="$JENKINS_BACKUP_DIR/$JENKINS_LATEST_BACKUP"
GRAFANA_BACKUP_FILE="$GRAFANA_BACKUP_DIR/$GRAFANA_LATEST_BACKUP"

# Jenkins
if [ -f "$JENKINS_BACKUP_FILE" ]; then
    echo "Wiederherstellen aus: $JENKINS_BACKUP_FILE"
    docker stop jenkins
    rm -rf /var/lib/docker/volumes/permajenkins_jenkins_home/_data/*
    tar -xzvf "$JENKINS_BACKUP_FILE" -C /var/lib/docker/volumes/permajenkins_jenkins_home/_data
    docker start jenkins
    echo "Wiederherstellung abgeschlossen!"
else
    echo "Kein Backup gefunden!"
fi

# grafana
if [ -f "$GRAFANA_BACKUP_FILE" ]; then
    echo "Wiederherstellen aus: $GRAFANA_BACKUP_FILE"
    docker stop grafana
    rm -rf /var/lib/docker/volumes/permajenkins_grafana_data/_data/*
    tar -xzvf "$GRAFANA_BACKUP_FILE" -C /var/lib/docker/volumes/permajenkins_grafana_data/_data
    docker start grafana
    echo "Wiederherstellung abgeschlossen!"
else
    echo "Kein Backup gefunden!"
fi
