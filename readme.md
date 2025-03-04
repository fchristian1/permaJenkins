# jenkins with grafana and prometheus

A FullAuto docker compose Jenkis runner with Monitoring and Backup

## Local Setup with Websocket Server and Client

github webhook -> websocket server -> websocket client -> jenkins

To use a local jenKins with github puch webhook, you need a host in the internet with linux docker to run the websocket-server.
On the local machine you need to run the websocket-client.

In Jenkins need to install the plugin "Generic Webhook Trigger Plugin" and configure it to use the websocket-client. use the Token for the URL of the repository to trigger the right jenkins job.


## Using

Startup the jenkins with grafana and prometheus
```bash
git clone https://github.com/fchristian1/permaJenkins
cd permaJenkins
./start.sh
```
In the browser go to http://<ip>:18080 for jenkins      http://localhost:18080
In the browser go to http://<ip>:13000 for grafana      http://localhost:13000
In the browser go to http://<ip>:19090 for prometheus   http://localhost:19090

Backup scripts are in the backup folder
To Backup the jenkins data
```bash
cd backup
./backup.sh
```
To Restore the jenkins data
```bash
cd backup
./restore.sh
```

To add backup to cron.d
```bash
./addCronJob.sh
```

## Using with websocket server and client

On Server
```bash
git clone https://github.com/fchristian1/permaJenkins
cd permaJenkins/websocket/server
docker compose up -d
```

On Client
copy the env file to .env and edit the file
```bash
git clone https://github.com/fchristian1/permaJenkins
cd permaJenkins/websocket/client
cp env .env
vi .env
docker compose up -d
```
