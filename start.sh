#!/bin/bash

docker_install=0

#check docker and docker compose installation
if ! command -v docker &>/dev/null; then
    echo "Docker is not installed. Please install Docker first."
    docker_install=1
fi

if ! command -v docker compose &>/dev/null; then
    echo "Docker Compose is not installed. Please install Docker Compose first."
    docker_install=1
fi

if [ "$docker_install" -eq 1 ]; then
    sudo apt-get install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
    curl -fsSL https://get.docker.com -o get-docker.sh
    sudo sh get-docker.sh
    sudo usermod -aG docker "$USER"

    # Automatisch als neuer Benutzer mit Docker-Berechtigung ausführen
    sudo -u "$USER" bash -c 'docker compose up -d'
fi

if [ "$docker_install" -eq 0 ]; then
    docker compose up -d
fi
