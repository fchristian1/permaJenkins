import WebSocket from "ws";

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
        console.log('received: ' + data);
        if (data != 'something') {
            //data is a completet express request object
            //send it to the server
            fetch('http://192.168.178.10:18080/jenkins/github-webhook/', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.parse(data.payload),
            })
                .then(response => response.text())
                .then(data => {
                    console.log('Success:', data);
                })
                .catch((error) => {
                    console.error('Error:', error);
                });
        }
    });
    ws.on('close', function close() {
        console.log('disconnected: ' + Date.now());
        setTimeout(connect, 1000);
    });
    return ws;
}