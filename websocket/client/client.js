import WebSocket from "ws";
import { config } from "dotenv";

config();

const JENKINS_URL = process.env.JENKINS_URL ?? console.error('JENKINS_URL not found in .env file');
const JENKINS_USER = process.env.JENKINS_USER ?? console.error('JENKINS_USER not found in .env file');
const JENKINS_TOKEN = process.env.JENKINS_TOKEN ?? console.error('JENKINS_TOKEN not found in .env file');
const WEBSOCKET_URL = process.env.WEBSOCKET_URL ?? console.error('WEBSOCKET_URL not found in .env file');

const ws = connect();

// Intervallfunktion zum Verbinden mit dem Server
function connect() {
    const ws = new WebSocket(WEBSOCKET_URL);
    ws.on("[" + Date.now() + "] [ERROR]", console.error);
    ws.on('open', function open() {
        console.log("[" + Date.now() + "] [INFO]", 'connected');
        ws.send('something');
    });
    ws.on('message', function message(data) {
        console.log("[" + Date.now() + "] [INFO]", 'received: %s', data.toString().includes('githubTrigger'));

        if (data.toString().includes('githubTrigger') && JSON.parse(data.toString()).type == 'githubTrigger') {
            console.log("[" + Date.now() + "] [INFO]", 'githubTrigger');
            sendToJenkins(JSON.parse(data.toString()).payload, JSON.parse(data.toString()).headers);
        }
    });
    ws.on('close', function close() {
        console.log("[" + Date.now() + "] [INFO]", 'disconnected: ' + Date.now());
        setTimeout(connect, 1000);
    });
    return ws;
}

async function getCrumb() {
    const response = await fetch(`${JENKINS_URL}/crumbIssuer/api/json`, {
        method: 'GET',
        headers: {
            'Authorization': 'Basic ' + Buffer.from(`${JENKINS_USER}:${JENKINS_TOKEN}`).toString('base64'),
        },
    });
    if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
    }
    return await response.json();
}

async function sendToJenkins(webhookData, headers) {
    try {
        console.log("[" + Date.now() + "] [INFO]", 'sendToJenkins');
        const crumbData = await getCrumb();
        const fetchData = {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Basic ' + Buffer.from(`${JENKINS_USER}:${JENKINS_TOKEN}`).toString('base64'),
                [crumbData.crumbRequestField]: crumbData.crumb,
                "x-GitHub-Delivery": headers['x-github-delivery'],
                "x-GitHub-Event": headers['x-github-event'],
                "x-github-hook-id": headers['x-github-hook-id'],
                "x-github-hook-installation-target-id": headers['x-github-hook-installation-target-id'],
                "x-github-hook-installation-target-type": headers['x-github-hook-installation-target-type'],
                token: webhookData.repository.url,
            },
            body: JSON.stringify(webhookData),
        };
        //console.log('url: ', `${JENKINS_URL}/generic-webhook-trigger/invoke`, 'fetchData:', JSON.stringify(webhookData, null, 2));
        const response = await fetch(`${JENKINS_URL}/generic-webhook-trigger/invoke`, fetchData);
        if (!response.ok) {
            console.log("[" + Date.now() + "] [ERROR]", await response.text());
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        console.log("[" + Date.now() + "] [INFO]", 'Success send Data to Jenkins');
    } catch (error) {
        console.error("[" + Date.now() + "] [ERROR]", error);
    }

}