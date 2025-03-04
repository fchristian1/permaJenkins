# Jenkins with Grafana and Prometheus

## Full-Auto Docker Compose Jenkins Runner with Monitoring and Backup

### Features:
- **Full-Auto Installation**: Jenkins, Grafana, and Prometheus are installed and configured automatically.
- **Preconfigured Jenkins Setup**:
  - Jenkins is preconfigured with **user and group permissions**.
  - **Default Credentials Jenkins**:
    - Username: `admin`
    - Password: `admin`
- **Preinstalled Grafana Dashboard**: Comes with a ready-to-use monitoring dashboard.
- **Backup & Restore**:
  - Fully automated scripts to **backup** and **restore** the Jenkins state.
  - Includes a cron job script to schedule backups automatically.
- **CI/CD Webhook Integration**:
  - Uses a **WebSocket server and client** to enable **GitHub webhook triggers** in a local environment **without exposing ports** or requiring GitHub credentials.
  - Ensures **secure and seamless communication** between GitHub and Jenkins.
- **Monitoring Setup**:
  - Jenkins is connected to **Grafana** and **Prometheus** for real-time monitoring and analytics.
  - Automatically tracks system performance and pipeline executions.
  - **Default Credentials Grafana**:
    - Username: `admin`
    - Password: `admin`
- **Ready-to-Use & Scalable**: The entire setup is automated, making it easy to deploy and scale.

---

## Local Setup with WebSocket Server and Client

#### Workflow:
GitHub Webhook -> WebSocket Server -> WebSocket Client -> Jenkins

To use a local Jenkins instance with GitHub push webhooks, you need an internet-accessible host running a WebSocket server on a Linux machine with Docker. On your local machine, you must run the WebSocket client.

#### Why This Approach?
- When working in a **local environment**, it's often necessary to trigger Jenkins builds using GitHub webhooks **without exposing network ports** or using GitHub credentials.
- Security concerns prevent direct network exposure, making it difficult to receive webhook calls locally.
- Using a WebSocket server as a bridge allows communication between GitHub and the local Jenkins instance **without requiring direct access to the local machine**.
- This method avoids complex networking setups, VPNs, or exposing Jenkins publicly, improving overall security and maintainability.

### Jenkins Configuration:
1. Install the **Generic Webhook Trigger Plugin** in Jenkins.
2. Configure it to use the WebSocket client.
3. Use the generated token in the repository webhook URL to trigger the correct Jenkins job.

---

## Usage

### Start Jenkins with Grafana and Prometheus
```bash
git clone https://github.com/fchristian1/permaJenkins
cd permaJenkins
./start.sh
```

### Access Web Interfaces:
- **Jenkins**: [http://localhost:18080](http://localhost:18080)
- **Grafana**: [http://localhost:13000](http://localhost:13000)
- **Prometheus**: [http://localhost:19090](http://localhost:19090)

---

## Backup & Restore

### Backup Jenkins Data:
```bash
cd backup
./backup.sh
```

### Restore Jenkins Data:
```bash
cd backup
./restore.sh
```

### Automate Backup with Cron:
```bash
./addCronJob.sh
```

---

## WebSocket Server and Client Setup

### On the Server:
```bash
git clone https://github.com/fchristian1/permaJenkins
cd permaJenkins/websocket/server
docker compose up -d
```

### On the Client:
1. Copy and edit the environment file:
```bash
git clone https://github.com/fchristian1/permaJenkins
cd permaJenkins/websocket/client
cp env .env
vi .env
```
2. Start the WebSocket client:
```bash
docker compose up -d
```

---

This setup provides an automated CI/CD environment with monitoring and backup capabilities, integrating Jenkins with GitHub webhooks via WebSocket communication while maintaining security and flexibility in local development setups.

