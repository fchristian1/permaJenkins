#!/bin/bash
docker compose up -d
docker compose exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
