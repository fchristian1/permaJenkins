#!/bin/bash

VOLUME_NAME="jenkins_home"

echo "📢 Achtung! Das gesamte Jenkins-Volume ($VOLUME_NAME) wird gelöscht!"
read -p "Bist du sicher? (ja/nein): " confirm

if [ "$confirm" != "ja" ]; then
    echo "❌ Abbruch! Keine Daten wurden gelöscht."
    exit 1
fi

echo "🛑 Stoppe Jenkins-Container..."
docker-compose down

echo "🗑️ Lösche das Docker-Volume '$VOLUME_NAME'..."
docker volume rm $VOLUME_NAME

echo "✅ Volume gelöscht! Jenkins kann nun frisch gestartet werden."

read -p "Möchtest du Jenkins neu starten? (ja/nein): " restart

if [ "$restart" == "ja" ]; then
    echo "🚀 Starte Jenkins neu..."
    docker-compose up -d
    echo "🎉 Jenkins läuft wieder mit einem frischen Volume!"
else
    echo "👌 Kein Neustart durchgeführt. Jenkins bleibt gestoppt."
fi
