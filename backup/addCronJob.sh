#/bin/bash
# add ./backup.sh to cronjob to run every day at 2am

# create a new cronjob
sudo echo "0 2 * * * root $(pwd)/backup.sh" >/etc/cron.d/jenkinsBackup
