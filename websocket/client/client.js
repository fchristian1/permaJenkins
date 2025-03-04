import WebSocket from "ws";
import { config } from "dotenv";

config();

const ws = connect();

const JENKINS_URL = process.env.JENKINS_URL ?? console.error('JENKINS_URL not found in .env file');
const JENKINS_USER = process.env.JENKINS_USER ?? console.error('JENKINS_USER not found in .env file');
const JENKINS_TOKEN = process.env.JENKINS_TOKEN ?? console.error('JENKINS_TOKEN not found in .env file');

// Intervallfunktion zum Verbinden mit dem Server
function connect() {
    const ws = new WebSocket('ws://85.209.49.32:28080');
    ws.on('error', console.error);
    ws.on('open', function open() {
        console.log('connected');
        ws.send('something');
    });
    ws.on('message', function message(data) {
        //console.log('received: %s', data);
        console.log('received: %s', data.toString().includes('githubTrigger'));

        if (data.toString().includes('githubTrigger') && JSON.parse(data.toString()).type == 'githubTrigger') {
            console.log('githubTrigger', Date.now());
            //data is a completet express request object
            //send it to the server
            sendToJenkins(JSON.parse(data.toString()).payload);
        }
    });
    ws.on('close', function close() {
        console.log('disconnected: ' + Date.now());
        setTimeout(connect, 1000);
    });
    return ws;
}

async function getCrump() {


    const response = await fetch(`${JENKINS_URL}/crumbIssuer/api/json`, {
        method: 'GET',
        headers: {
            'Authorization': 'Basic ' + btoa(`${JENKINS_USER}:${JENKINS_TOKEN}`),
        },
    });
    if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
    }
    return await response.json();
}

async function sendToJenkins(webhookData) {
    try {
        const crumbData = await getCrump();
        const response = await fetch(`${JENKINS_URL}/github-webhook/`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                "Authorization": 'Basic ' + btoa(`${JENKINS_USER}:${JENKINS_TOKEN}`),
                [crumbData.crumbRequestField]: crumbData.crumb,
            },
            body: JSON.stringify(webhookData),
        });
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${JSON.stringify(response.body)}`);
        }
        console.log('Success send Data to Jenkins:', response);

    } catch (error) {
        console.error('Error:', error);
    }
}