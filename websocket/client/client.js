import WebSocket from "ws";

const ws = connect();

// intervall function to connect to the server
function connect() {
    const ws = new WebSocket('ws://localhost:28080');
    ws.on('error', console.error);
    ws.on('open', function open() {
        console.log('connected');
        ws.send('something');
    });
    ws.on('message', function message(data) {
        console.log('recived: %s', data);
    });
    ws.on('close', function close() {
        console.log('disconnected: ' + Date.now());
        setTimeout(connect, 1000);
    });
    return ws;
}