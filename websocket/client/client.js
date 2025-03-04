import WebSocket from "ws";
import { config } from "dotenv";

config();

const JENKINS_URL = process.env.JENKINS_URL ?? console.error('JENKINS_URL not found in .env file');
const JENKINS_USER = process.env.JENKINS_USER ?? console.error('JENKINS_USER not found in .env file');
const JENKINS_TOKEN = process.env.JENKINS_TOKEN ?? console.error('JENKINS_TOKEN not found in .env file');

const ws = connect();

// Intervallfunktion zum Verbinden mit dem Server
function connect() {
    const ws = new WebSocket('ws://85.209.49.32:28080');
    ws.on('error', console.error);
    ws.on('open', function open() {
        console.log('connected');
        ws.send('something');
    });
    ws.on('message', function message(data) {
        console.log('received: %s', data.toString().includes('githubTrigger'));

        if (data.toString().includes('githubTrigger') && JSON.parse(data.toString()).type == 'githubTrigger') {
            console.log('githubTrigger', Date.now());
            sendToJenkins(JSON.parse(data.toString()).payload);
        }
    });
    ws.on('close', function close() {
        console.log('disconnected: ' + Date.now());
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

async function sendToJenkins(webhookData) {
    try {
        const crumbData = await getCrumb();
        const fetchData = {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                // 'Authorization': 'Basic ' + Buffer.from(`${JENKINS_USER}:${JENKINS_TOKEN}`).toString('base64'),
                // [crumbData.crumbRequestField]: crumbData.crumb,
            },
            body: JSON.stringify(webhookData),
        };
        console.log('url: ', `${JENKINS_URL}/github-webhook/`, 'fetchData:', fetchData.headers);
        const response = await fetch(`${JENKINS_URL}/github-webhook/`, fetchData);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        console.log('Success send Data to Jenkins:', response);

    } catch (error) {
        console.error('Error:', error);
    }
}