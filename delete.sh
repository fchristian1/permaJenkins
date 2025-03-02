#!/bin/bash

JENKINS_VOLUME_NAME="permajenkins_jenkins_home"
GRAFANA_VOLUME_NAME="permajenkins_grafana_data"

echo "📢 Achtung! Alle Jenkins/Grafana-Volumes ($JENKINS_VOLUME_NAME, $GRAFANA_VOLUME_NAME) wird gelöscht!"
read -p "Bist du sicher? (ja/nein): " confirm

if [ "$confirm" != "ja" ]; then
    echo "❌ Abbruch! Keine Daten wurden gelöscht."
    exit 1
fi

echo "🛑 Stoppe Jenkins/Grafana/Prometheus-Container..."
docker compose down

echo "🗑️ Lösche die Docker-Volumes '$JENKINS_VOLUME_NAME, $GRAFANA_VOLUME_NAME'..."
docker volume rm $JENKINS_VOLUME_NAME $GRAFANA_VOLUME_NAME

echo "✅ Volumes gelöscht! Jenkins/Grafana/Prometheus kann nun frisch gestartet werden."

read -p "Möchtest du Jenkins/Grafana/Prometheus neu starten? (ja/nein): " restart

if [ "$restart" == "ja" ]; then
    echo "🚀 Starte Jenkins/Grafana/Prometheus neu..."
    docker compose up -d
    echo "🎉 Jenkins/Grafana/Prometheus läuft wieder mit einem frischen Volume!"
else
    echo "👌 Kein Neustart durchgeführt. Jenkins/Grafana/Prometheus bleibt gestoppt."
fi
