# jenkins with grafana and prometheus

A FullAuto docker compose Jenkis runner with Monitoring and Backup

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